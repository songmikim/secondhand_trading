package org.koreait.survey.diabetes.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.configs.PythonProperties;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(PythonProperties.class)
public class DiabetesSurveyPredictService {
    private final PythonProperties properties;
    private final WebApplicationContext ctx;
    private final ObjectMapper om;

    public List<Integer> process(List<List<Number>> items) {

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

                String data = om.writeValueAsString(items);

                builder = new ProcessBuilder(pythonPath, properties.getRestaurant() + "/diabetes/predict.py", data);
                process = builder.start();
                int statusCode = process.waitFor();
                if (statusCode == 0) {
                    String json = process.inputReader().lines().collect(Collectors.joining());
                    return om.readValue(json, new TypeReference<>() {});

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

    /**
     * 설문 하나에 대한 당뇨병 설문 결과
     *
     * @param item
     * @return
     */
    public boolean isDiabetes(List<Number> item) {
        List<Integer> results = process(List.of(item));

        return !results.isEmpty() && results.getFirst() == 1;
    }

    public boolean isDiabetes(RequestDiabetesSurvey form) {
        List<Number> item = new ArrayList<>();
        item.add(form.getGender().getNum());
        item.add(form.getAge());
        item.add(form.isHypertension() ? 1 : 0);
        item.add(form.isHeartDisease() ? 1 : 0);
        item.add(form.getSmokingHistory().getNum());

        // BMI 지수 계산
        double height = form.getHeight() / 100.0;
        double weight = form.getWeight();

        double bmi = Math.round((weight / Math.pow(height, 2.0)) * 100.0) / 100.0;
        item.add(bmi);

        item.add(form.getHbA1c()); // 당화혈색소 수치
        item.add(form.getBloodGlucoseLevel()); // 혈당 수치

        return isDiabetes(item);
    }
}