package org.koreait.restaurant.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.configs.PythonProperties;
import org.koreait.restaurant.controllers.RestaurantSearch;
import org.koreait.restaurant.entities.Restaurant;
import org.koreait.restaurant.repositories.RestaurantRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 식당 정보 조회 및 검색 처리를 담당하는 서비스 클래스
 */
@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(PythonProperties.class)
public class RestaurantInfoService {

    private final PythonProperties properties;
    private final WebApplicationContext ctx;
    private final RestaurantRepository repository;
    private final ObjectMapper om;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 지정된 좌표(lat, lon) 주변에서 cnt개의 식당을 Python 스크립트를 통해 추천
     * @param lat 조회할 중심 위도
     * @param lon 조회할 중심 경도
     * @param cnt 반환할 식당 수
     * @return 추천된 Restaurant 엔티티 목록 (실패 시 빈 리스트)
     */
    public List<Restaurant> getNestest(double lat, double lon, int cnt) {
        if (lat == 0.0 || lon == 0.0) {
            return List.of(); // 좌표 미입력 시 빈 결과 반환
        }

        // 운영 환경 여부 확인 (prod 또는 mac 프로파일)
        boolean isProduction = Arrays.stream(ctx.getEnvironment().getActiveProfiles()).anyMatch(s -> s.equals("prod") || s.equals("mac"));

        String activationCommand = null, pythonPath = null;
        if (isProduction) { // 리눅스 환경: bash 활성화 스크립트
            activationCommand = String.format("%s/activate", properties.getBase2());
            pythonPath = properties.getBase2() + "/python";
        } else { // 윈도우 환경: .bat 활성화 스크립트
            activationCommand = String.format("%s/activate.bat", properties.getBase2());
            pythonPath = properties.getBase2() + "/python.exe";
        }

        try {
            ProcessBuilder builder = isProduction ? new ProcessBuilder("/bin/sh", activationCommand) : new ProcessBuilder(activationCommand); // 가상환경 활성화
            Process process = builder.start();
            if (process.waitFor() == 0) { // 정상 수행된 경우
                builder = new ProcessBuilder(pythonPath, properties.getRestaurant() + "/search.py", "" + lat, "" + lon, "" + cnt);
                process = builder.start();
                int statusCode = process.waitFor();
                if (statusCode == 0) {
                    // 결과 JSON 파싱 후 DB 조회
                    String json = process.inputReader().lines().collect(Collectors.joining());
                    List<Long> seqs = om.readValue(json, new TypeReference<>() {});
                    return repository.findAllById(seqs);

                } else {// 에러 로깅
                    System.out.println("statusCode:" + statusCode);
                    process.errorReader().lines().forEach(System.out::println);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }

    /**
     * getNestest 호출: 주변 10개 식당 추천
     * @param lat 중심 위도
     * @param lon 중심 경도
     * @return 최대 10개의 Restaurant 목록
     */
    public List<Restaurant> getNearest(double lat, double lon) {
        return getNestest(lat, lon, 10);
    }

    /**
     * RestaurantSearch 객체를 이용한 주변 식당 추천
     * @param search 검색 조건(lat, lon, cnt)
     * @return 추천된 Restaurant 목록
     */
    public List<Restaurant> getNearest(RestaurantSearch search) {
        int cnt = search.getCnt();
        cnt = cnt < 1 ? 10 : cnt;
        return getNestest(search.getLat(), search.getLon(), cnt);
    }

    /**
     * 키워드와 위치 정보를 조합한 식당 검색
     * @param search 검색 조건(skey, lat, lon, cnt)
     * @return 검색된 Restaurant 목록
     */
    public List<Restaurant> search(RestaurantSearch search) {
        String skey = search.getSkey();
        int cnt = search.getCnt();
        cnt = cnt < 1 ? 10 : cnt;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM RESTAURANT");

        List<Object> params = new ArrayList<>();

        if (StringUtils.hasText(skey)) {
            sb.append(" WHERE name LIKE ? OR address LIKE ? OR roadAddress LIKE ?");
            for (int i = 0; i < 3; i++) params.add("%" + skey + "%");
        }

        if (search.getLat() != 0 && search.getLon() != 0) {
            sb.append(params.isEmpty() ? " WHERE " : " AND ");
            sb.append("1=1 ORDER BY POWER(lat - ?, 2) + POWER(lon - ?, 2)");
            params.add(search.getLat());
            params.add(search.getLon());
        }

        sb.append(" LIMIT ?");
        params.add(cnt);

        return jdbcTemplate.query(sb.toString(), this::mapper, params.toArray());
    }

    /**
     * JDBC 쿼리 결과를 Restaurant 엔티티로 매핑
     * @param rs ResultSet 객체
     * @param i  현재 행 번호 (row index)
     * @return   매핑된 Restaurant 객체
     * @throws SQLException 매핑 오류 시 발생
     */
    private Restaurant mapper(ResultSet rs, int i) throws SQLException {
        Restaurant item = new Restaurant();
        item.setSeq(rs.getLong("seq"));
        item.setZipcode(rs.getString("zipcode"));
        item.setAddress(rs.getString("address"));
        item.setZonecode(rs.getString("zonecode"));
        item.setRoadAddress(rs.getString("roadAddress"));
        item.setCategory(rs.getString("category"));
        item.setName(rs.getString("name"));
        item.setLat(rs.getDouble("lat"));
        item.setLon(rs.getDouble("lon"));

        return item;
    }
}