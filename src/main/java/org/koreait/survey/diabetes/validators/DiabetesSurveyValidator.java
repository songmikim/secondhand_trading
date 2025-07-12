package org.koreait.survey.diabetes.validators;

import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
/**
 * 당뇨 설문 단계별 입력값 검증을 수행하는 Validator 클래스
 * <ul>
 *   <li>step1: 나이 검증 (5세~130세)</li>
 *   <li>step2: 키/몸무게/HbA1c/혈당 수치 검증</li>
 * </ul>
 */
@Component
public class DiabetesSurveyValidator implements Validator {

    /**
     * Validator가 지원하는 클래스 타입인지 여부 확인
     * @param clazz 검증 대상 클래스 객체
     * @return RequestDiabetesSurvey 타입이면 true
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestDiabetesSurvey.class);
    }

    /**
     * 폼 데이터 검증 로직 진입점
     * @param target 검증 대상 폼 객체
     * @param errors 검증 오류 정보를 저장할 Errors 객체
     */
    @Override
    public void validate(Object target, Errors errors) {
        RequestDiabetesSurvey form = (RequestDiabetesSurvey) target;
        String mode = form.getMode();
        // step2 모드면 상세 항목 검증, 아니면 step1 기본 항목 검증
        if (StringUtils.hasText(mode) && mode.equals("step2")) { // step2 검증
            validateStep2(form, errors);
        } else { // step1 검증
            validateStep1(form, errors);
        }
    }

    /**
     * step1 검증: 나이 범위 확인
     * @param form 설문 폼 데이터
     * @param errors 오류 저장 객체
     */
    private void validateStep1(RequestDiabetesSurvey form, Errors errors) {
        // 유효 나이 범위: 5세 이상, 130세 이하
        int age = form.getAge();
        if (age < 5 || age > 130) {
            errors.rejectValue("age", "Size");
        }

    }

    /**
     * step2 검증: 키, 몸무게, HbA1c, 혈당 수치 확인
     * @param form 설문 폼 데이터
     * @param errors 오류 저장 객체
     */
    private void validateStep2(RequestDiabetesSurvey form, Errors errors) {

        double height = form.getHeight();
        double weight = form.getWeight();
        double hbA1c = form.getHbA1c();
        double bloodGlucoseLevel = form.getBloodGlucoseLevel();

        // 키 유효 범위: 50cm~350cm
        if (height < 50.0 || height > 350.0) {
            errors.rejectValue("height", "Size");
        }

        // 몸무게 유효 범위: 10kg~450kg
        if (weight < 10.0 || weight > 450.0) {
            errors.rejectValue("weight", "Size");
        }

        // HbA1c 유효 범위: 0%~100%
        if (hbA1c < 0.0 || hbA1c > 100.0) {
            errors.rejectValue("hbA1c", "Size");
        }

        if (bloodGlucoseLevel < 0.0) {
            errors.rejectValue("bloodGlucoseLevel", "Size");
        }
    }
}