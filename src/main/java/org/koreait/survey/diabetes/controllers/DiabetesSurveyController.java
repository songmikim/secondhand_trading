package org.koreait.survey.diabetes.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.constants.Gender;
import org.koreait.global.libs.Utils;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.koreait.survey.diabetes.services.DiabetesSurveyInfoService;
import org.koreait.survey.diabetes.services.DiabetesSurveyService;
import org.koreait.survey.diabetes.validators.DiabetesSurveyValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

/**
 * 당뇨 고위험군 설문 흐름을 관리하는 컨트롤러
 * <ul>
 *   <li>1단계: 초기 설문 폼 표시</li>
 *   <li>2단계: 폼 검증 후 다음 단계 표시</li>
 *   <li>설문 처리: 결과 저장 및 결과 페이지로 리다이렉트</li>
 *   <li>결과 보기: 저장된 설문 결과 표시</li>
 * </ul>
 */
@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/survey/diabetes")
@SessionAttributes("requestDiabetesSurvey")
public class DiabetesSurveyController {

    private final Utils utils;
    private final DiabetesSurveyValidator validator;
    private final DiabetesSurveyService surveyService;
    private final DiabetesSurveyInfoService infoService;

    /**
     * 설문 페이지에 적용할 CSS 파일 목록을 제공
     * @return CSS 경로 리스트
     */
    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("survey/diabetes/style");
    }

    /**
     * 세션에 저장될 설문 폼 객체를 초기화하고 기본값 설정
     * @return 기본값이 설정된 RequestDiabetesSurvey 객체
     */
    @ModelAttribute("requestDiabetesSurvey")
    public RequestDiabetesSurvey requestDiabetesSurvey() {
        RequestDiabetesSurvey form = new RequestDiabetesSurvey();
        form.setGender(Gender.FEMALE);
        form.setSmokingHistory(SmokingHistory.CURRENT);

        return form;
    }

    /**
     * 성별 선택 옵션을 제공
     * @return Gender 열거형 값 배열
     */
    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    /**
     * 흡연 이력 선택 옵션을 제공
     * @return SmokingHistory 열거형 값 배열
     */
    @ModelAttribute("smokingHistories")
    public SmokingHistory[] smokingHistories() {
        return SmokingHistory.values();
    }

    /**
     * 설문 1단계 폼 표시
     * @param form 세션에서 가져온 폼 데이터
     * @param model 뷰에 전달할 모델
     * @return step1 템플릿 경로
     */
    @GetMapping({"","/step1"})
    public String step1(@ModelAttribute RequestDiabetesSurvey form, Model model) {
        commonProcess("step", model);

        return utils.tpl("survey/diabetes/step1");
    }

    /**
     * 1단계 폼 제출 처리 및 2단계 이동
     * @param form 사용자 입력이 바인딩된 폼
     * @param errors 검증 에러 객체
     * @param model 뷰에 전달할 모델
     * @return 오류 시 다시 step1, 정상 시 step2 템플릿
     */
    @PostMapping("/step2")
    public String step2(@Valid RequestDiabetesSurvey form, Errors errors, Model model) {
        commonProcess("step", model);

        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("survey/diabetes/step1");
        }

        return utils.tpl("survey/diabetes/step2");
    }

    /**
     * 최종 설문 제출 처리: 검증, 저장, 결과 페이지 리다이렉트
     * @param form 검증된 설문 폼
     * @param errors 검증 에러 객체
     * @param model 뷰에 전달할 모델
     * @param status 세션 상태 제어 객체
     * @return 결과 페이지로 리다이렉트 URL
     */
    @PostMapping("/process")
    public String process(@Valid RequestDiabetesSurvey form, Errors errors, Model model, SessionStatus status) {
        commonProcess("step", model);

        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("survey/diabetes/step2");
        }

        // 설문 결과 및 저장 처리
        DiabetesSurvey item = surveyService.process(form);

        // 처리 완료 후 세션값으로 더이상 변경되지 않도록 완료 처리
        status.setComplete();

        // 양식데이터 초기화
        model.addAttribute("requestDiabetesSurvey", requestDiabetesSurvey());

        return "redirect:/survey/diabetes/result/" + item.getSeq();
    }


    /**
     * 설문 결과 페이지 표시
     * @param seq 저장된 설문 ID
     * @param model 뷰에 전달할 모델
     * @return result 템플릿 경로
     */
    @GetMapping("/result/{seq}")
    public String result(@PathVariable("seq") Long seq, Model model) {
        commonProcess("result", model);

        DiabetesSurvey item = infoService.get(seq);
        model.addAttribute("item", item);


        return utils.tpl("survey/diabetes/result");
    }


    /**
     * 공통 속성(페이지 제목 등) 설정 메서드
     * @param mode "step" 또는 "result" 모드 구분
     * @param model 뷰에 전달할 모델
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "step";
        String pageTitle = "";
        if (mode.equals("step")) {
            pageTitle = utils.getMessage("당뇨_고위험군_테스트");

        } else if (mode.equals("result")) {
            pageTitle = utils.getMessage("당뇨_고위험군_테스트_결과");
        }

        model.addAttribute("pageTitle", pageTitle);
    }
}