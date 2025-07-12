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

/**
 * 당뇨 설문 예측 처리를 담당하는 서비스 클래스
 */
@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(PythonProperties.class)
public class DiabetesSurveyPredictService {
    private final PythonProperties properties;  // Python 가상환경 및 스크립트 경로 설정
    private final WebApplicationContext ctx;    // Spring 프로파일 정보 조회용
    private final ObjectMapper om;              // JSON 직렬화/역직렬화 유틸

    /**
     * 여러 설문 데이터를 Python 예측 모델에 전달하여 결과 리스트를 반환
     *
     * @param items 예측할 설문 데이터 목록 (각 항목은 Number 타입 리스트)
     * @return 예측 결과 코드 리스트 (0: 비위험, 1: 위험)
     */
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
     * 단일 설문 항목을 기반으로 당뇨 위험 여부를 판단
     *
     * @param item 하나의 설문 데이터 리스트 (Number 타입)
     * @return true면 당뇨 위험군, false면 정상
     */
    public boolean isDiabetes(List<Number> item) {
        List<Integer> results = process(List.of(item));

        return !results.isEmpty() && results.getFirst() == 1;
    }

    /**
     * 폼 객체를 기반으로 설문 데이터를 생성하여 당뇨 위험 여부를 판단
     *
     * @param form 사용자 입력 폼(RequestDiabetesSurvey)
     * @return true면 당뇨 위험군, false면 정상
     */
    public boolean isDiabetes(RequestDiabetesSurvey form) {
        List<Number> item = new ArrayList<>();
        item.add(form.getGender().getNum());
        item.add(form.getAge());
        item.add(form.isHypertension() ? 1 : 0);
        item.add(form.isHeartDisease() ? 1 : 0);
        item.add(form.getSmokingHistory().getNum());

        // BMI 지수 계산
        double bmi = getBmi(form.getHeight(), form.getWeight());
        item.add(bmi);

        item.add(form.getHbA1c()); // 당화혈색소 수치
        item.add(form.getBloodGlucoseLevel()); // 혈당 수치

        return isDiabetes(item);
    }

    /**
     * 신체 지수 지수(BMI)를 계산
     *
     * @param height 키(cm)
     * @param weight 몸무게(kg)
     * @return 계산된 BMI 값 (소수점 이하 둘째 자리 반올림)
     */
    public double getBmi(double height, double weight){
        height = height / 100.0;

        return Math.round((weight / Math.pow(height, 2.0)) * 100.0) / 100.0;
    }
}