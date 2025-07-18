package org.koreait.trend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.configs.FileProperties;
import org.koreait.global.configs.PythonProperties;
import org.koreait.trend.entities.CollectedTrend;
import org.koreait.trend.entities.Trend;
import org.koreait.trend.entities.TrendUrl;
import org.koreait.trend.repositories.TrendRepository;
import org.koreait.trend.repositories.TrendUrlRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({PythonProperties.class, FileProperties.class})
public class TrendCollectService {

    private final PythonProperties properties;
    private final FileProperties fileProperties;

    private final WebApplicationContext ctx;
    private final TrendRepository repository;
    private final TrendUrlRepository urlRepository;
    private final ObjectMapper om;

    public CollectedTrend process(String url) {
        // spring.profiles.active=default,prod
        // 최초 유입된 url 이라면 저장 처리(news.naver.com은 제외)
        // 등록된 URL은 주기적으로 조회하게 됨
        if (!url.contains("news.naver.com") && !urlRepository.existsById(url)) {
           TrendUrl trendUrl = new TrendUrl();
           trendUrl.setSiteUrl(url);
           urlRepository.saveAndFlush(trendUrl);
        }

        boolean isProduction = Arrays.stream(ctx.getEnvironment().getActiveProfiles()).anyMatch(s -> s.equals("prod") || s.equals("mac"));

        String activationCommand = null, pythonPath = null;
        if (isProduction) { // 리눅스 환경, 서비스 환경
            activationCommand = String.format("%s/activate", properties.getBase());
            pythonPath = properties.getBase() + "/python";
        } else { // 윈도우즈 환경
            activationCommand = String.format("%s/activate.bat", properties.getBase());
            pythonPath = properties.getBase() + "/python.exe";
        }

        try {
            ProcessBuilder builder = isProduction ? new ProcessBuilder("/bin/sh", activationCommand) : new ProcessBuilder(activationCommand); // 가상환경 활성화
            Process process = builder.start();
            if (process.waitFor() == 0) { // 정상 수행된 경우
                builder = new ProcessBuilder(pythonPath, properties.getTrend() + "/trend.py", fileProperties.getPath() + "/trend", url);
                process = builder.start();
                int statusCode = process.waitFor();
                if (statusCode == 0) {
                    String json = process.inputReader().lines().collect(Collectors.joining());
                    CollectedTrend item = om.readValue(json, CollectedTrend.class);

                    String wordCloud = String.format("%s/trend/%s", fileProperties.getUrl(), item.getImage());
                    try {
                        String keywords = om.writeValueAsString(item.getKeywords());
                        Trend data = new Trend();
                        data.setCategory(url.contains("news.naver.com") ? "NEWS" : "" + Objects.hash(url));
                        data.setWordCloud(wordCloud);
                        data.setKeywords(keywords);
                        repository.save(data);
                    } catch (JsonProcessingException e) {}

                } else {
                    System.out.println("statusCode:" + statusCode);
                    process.errorReader().lines().forEach(System.out::println);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // 실패시에는 null
    }

    /**
     * JSON 문자열로 워드 클라우드 이미지 생성하기
     *
     * @param json
     * @return
     */
    public String createWordCloud(String json) {
        boolean isProduction = Arrays.stream(ctx.getEnvironment().getActiveProfiles()).anyMatch(s -> s.equals("prod") || s.equals("mac"));

        String activationCommand = null, pythonPath = null;
        if (isProduction) { // 리눅스 환경, 서비스 환경
            activationCommand = String.format("%s/activate", properties.getBase());
            pythonPath = properties.getBase() + "/python";
        } else { // 윈도우즈 환경
            activationCommand = String.format("%s/activate.bat", properties.getBase());
            pythonPath = properties.getBase() + "/python.exe";
        }

        try {
            String fileName = String.format("wc%d.jpg", Math.abs(Objects.hash(json)));
            String filePath = fileProperties.getPath() + "/trend/" + fileName;
            ProcessBuilder builder = isProduction ? new ProcessBuilder("/bin/sh", activationCommand) : new ProcessBuilder(activationCommand); // 가상환경 활성화
            Process process = builder.start();
            if (process.waitFor() == 0) { // 정상 수행된 경우
                builder = new ProcessBuilder(pythonPath, properties.getTrend() + "/generate_wordcloud.py", filePath, json);
                process = builder.start();
                int statusCode = process.waitFor();
                if (statusCode == 0) {
                    return fileProperties.getUrl() + "/trend/" + fileName;
                } else {
                    System.out.println("statusCode:" + statusCode);
                    process.errorReader().lines().forEach(System.out::println);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 매 1시간 마다 주기적으로 뉴스 및 저장된 주소 트렌드 수집
     *
     */
    //@Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    public void scheduledJob() {
            List<TrendUrl> trendUrls = urlRepository.findAll();
            List<String> urls = trendUrls == null ? new ArrayList<>() : new ArrayList<>(trendUrls.stream().map(TrendUrl::getSiteUrl).toList());
            urls.add("https://news.naver.com/");

            urls.forEach(this::process);
    }
}
