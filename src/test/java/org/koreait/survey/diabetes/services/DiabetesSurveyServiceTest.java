package org.koreait.survey.diabetes.services;

import org.junit.jupiter.api.Test;
import org.koreait.global.constants.Gender;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiabetesSurveyServiceTest {

    @Autowired
    private DiabetesSurveyService surveyService;

    @Test
    void test() {
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

        DiabetesSurvey item = surveyService.process(form);
        System.out.println(item);
    }
}