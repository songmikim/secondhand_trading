package org.koreait.survey.diabetes.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.koreait.global.constants.Gender;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiabetesSurveyPredictServiceTest {
    @Autowired
    private DiabetesSurveyPredictService service;

    @Autowired
    private ObjectMapper om;

    @Test
    void test() throws Exception {
        String json = "[[1.0, 46.0, 0.0, 0.0, 3.0, 29.6, 5.8, 130.0], [1.0, 69.0, 1.0, 0.0, 4.0, 29.52, 3.5, 140.0], [0.0, 50.0, 0.0, 0.0, 0.0, 27.32, 5.7, 140.0], [0.0, 74.0, 0.0, 0.0, 3.0, 25.53, 4.5, 159.0], [0.0, 80.0, 1.0, 0.0, 4.0, 21.17, 6.6, 145.0]]";

        List<List<Number>> items = om.readValue(json, new TypeReference<>() {});

        List<Integer> results = service.process(items);
        System.out.println(results);
    }

    @Test
    void test2() {
        //List<Number> item = List.of(1, 46.0, 0.0, 0.0, 3, 29.6, 5.8, 130);
        //List<Number> item = List.of(1, 41, 0, 0, 0,29.6, 8.2, 126);
        //boolean result = service.isDiabetes(item);
        //System.out.println(result);
        RequestDiabetesSurvey form = new RequestDiabetesSurvey();
        form.setGender(Gender.MALE);
        form.setAge(41);
        form.setHypertension(false);
        form.setHeartDisease(false);
        form.setSmokingHistory(SmokingHistory.EVER);
        form.setHeight(178.5);
        form.setWeight(120);
        form.setHbA1c(8.2);
        form.setBloodGlucoseLevel(126);

        boolean result = service.isDiabetes(form);
        System.out.println(result);
    }
}