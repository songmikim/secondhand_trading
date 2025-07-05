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
     * 주어진 좌표 근처의 cnt개 만큼의 식당 조회
     *
     * @param lat
     * @param lon
     * @param cnt
     * @return
     */
    public List<Restaurant> getNestest(double lat, double lon, int cnt) {
        if (lat == 0.0 || lon == 0.0) {
            return List.of();
        }

        boolean isProduction = Arrays.stream(ctx.getEnvironment().getActiveProfiles()).anyMatch(s -> s.equals("prod") || s.equals("mac"));

        String activationCommand = null, pythonPath = null;
        if (isProduction) { // 리눅스 환경, 서비스 환경
            activationCommand = String.format("%s/activate", properties.getBase2());
            pythonPath = properties.getBase2() + "/python";
        } else { // 윈도우즈 환경
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
                    String json = process.inputReader().lines().collect(Collectors.joining());
                    List<Long> seqs = om.readValue(json, new TypeReference<>() {});
                    return repository.findAllById(seqs);

                } else {
                    System.out.println("statusCode:" + statusCode);
                    process.errorReader().lines().forEach(System.out::println);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }

    public List<Restaurant> getNearest(double lat, double lon) {
        return getNestest(lat, lon, 10);
    }

    public List<Restaurant> getNearest(RestaurantSearch search) {
        int cnt = search.getCnt();
        cnt = cnt < 1 ? 10 : cnt;
        return getNestest(search.getLat(), search.getLon(), cnt);
    }

    /**
     * 키워드 기반 검색 - 식당명 또는 주소
     *
     * @param search
     * @return
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