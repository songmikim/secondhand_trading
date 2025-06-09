package org.koreait.trend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.search.CommonSearch;
import org.koreait.trend.entities.Trend;
import org.koreait.trend.exceptions.TrendNotFoundException;
import org.koreait.trend.repositories.TrendRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Lazy
@Service
@RequiredArgsConstructor
public class TrendInfoService {

    private final TrendRepository repository;
    private final TrendCollectService collectService;
    private final ObjectMapper om;
    /**
     * 최근 트렌드 1개 조회
     *
     * @param category
     * @return
     */
    public Trend getLatest(String category) {
        Trend item = repository.getLatest(category).orElseThrow(TrendNotFoundException::new);

        return item;
    }


    /**
     * 특정 날자의 트렌드 데이터 1개
     * @param date
     * @return
     */
    public Trend get(String category, LocalDate date) {
        return repository.get(category, date.atStartOfDay(), LocalDateTime.of(date, LocalTime.of(23, 59, 59))).orElse(null);
    }

    /**
     * 특정 날짜 범위의 트렌트 데이터 조회
     *
     * @return
     */
    public List<Trend> getList(String category, CommonSearch search) {
        LocalDate sDate = Objects.requireNonNullElse(search.getSDate(), LocalDate.now());
        LocalDate eDate = Objects.requireNonNullElse(search.getEDate(), LocalDate.now());
        List<Trend> data = repository.getList(category, sDate.atStartOfDay(), LocalDateTime.of(eDate, LocalTime.of(23, 59, 59)));
        return data;
    }

    /**
     * URL 정보로 트렌드 데이터 조회
     *
     * @param url
     * @return
     */
    public Map<String, Object> getStat(String url) {
        collectService.process(url); // 데이터 한번 수집

        Map<String, Object> statData = new HashMap<>(); // 통계 데이터

        String category = url.contains("news.naver.com") ? "NEWS" : "" + Objects.hash(url);
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1L); // 일주일 전
        LocalDate oneMonthAgo = today.minusMonths(1L); // 한달 전

        Trend now = get(category, today); // 조회 시점 데이터 수집
        statData.put("now", now);


        CommonSearch search = new CommonSearch();
        search.setEDate(today);

        /**
         * 통계 데이터는 일별 키워드의 조회수 평균을 구한다.
         *
         */
        // 일주일간 통계 S
        search.setSDate(oneWeekAgo);
        List<Trend> oneWeekItems = getList(category, search);
        Map<LocalDate, Map<String, Integer>> oneWeekData = preprocessing(oneWeekItems);
        statData.put("oneWeek", oneWeekData);
        statData.put("oneWeekWordCloud", getWordCloudPath(oneWeekData));
        // 일주일간 통계 E

        // 한달간 통계 S
        search.setSDate(oneMonthAgo);
        List<Trend> oneMonthItems = getList(category, search);
        Map<LocalDate, Map<String, Integer>> oneMonthData = preprocessing(oneMonthItems);
        statData.put("oneMonth", oneMonthData);
        statData.put("oneMonthWordCloud", getWordCloudPath(oneMonthData));
        // 한달간 통계 E


        // JSON 변환 데이터
        try {
            statData.put("json", om.writeValueAsString(statData));
        } catch (JsonProcessingException e) {}

        return statData;
    }

    private  Map<LocalDate, Map<String, Integer>> preprocessing(List<Trend> items) {
        if (items == null) return null;

        Map<LocalDate, Map<String, Integer>> itemsTotal = new HashMap<>(); // 키워드별 합계
        Map<LocalDate, Map<String, Integer>> itemsAvg = new TreeMap<>(); // 키워드별 통계
        Map<LocalDate, Map<String, Integer>> itemsCount = new HashMap<>(); // 키워드별 카운트
        for (Trend item : items) {
            LocalDate date = item.getCreatedAt().toLocalDate();
            Map<String, Integer> total = itemsTotal.getOrDefault(date, new HashMap<>());
            Map<String, Integer> avg = itemsAvg.getOrDefault(date, new HashMap<>());
            Map<String, Integer> count = itemsCount.getOrDefault(date, new HashMap<>());

            try {
                Map<String, Integer> keywords = om.readValue(item.getKeywords(), new TypeReference<>() {});
                keywords.forEach((key, value) -> {
                    int t = total.getOrDefault(key, 0) + value;
                    int c = count.getOrDefault(key, 0) + 1;
                    total.put(key, t); // 합계
                    count.put(key, c); // 일별 통계 카운트
                    avg.put(key, (int)Math.round(t / (double)c)); // 합계
                });
            } catch (JsonProcessingException e) {}

            itemsTotal.put(date, total);
            itemsCount.put(date, count);
            itemsAvg.put(date, avg);
        }

        return itemsAvg;
    }

    /**
     * 워드 클라우드 이미지 경로
     *
     * @param data
     * @return
     */
    public String getWordCloudPath(Map<LocalDate, Map<String, Integer>> data) {
        Map<String, Integer> items = new HashMap<>();
        data.values().stream().forEach(m -> {
            m.forEach((k, v) -> {

                int cnt = items.getOrDefault(k, 0) + v;
                items.put(k, cnt);
            });
        });

        try {
            String json = om.writeValueAsString(items);
            return collectService.createWordCloud(json);
        } catch (JsonProcessingException e) {}

        return null;
    }
}
