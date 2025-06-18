package org.koreait.survey.diabetes.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.constants.Gender;
import org.koreait.global.libs.Utils;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.services.DiabetesSurveyService;
import org.koreait.survey.diabetes.validators.DiabetesSurveyValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey/diabetes")
@SessionAttributes("requestDiabetesSurvey")
public class DiabetesSurveyController {

    private final Utils utils;
    private final DiabetesSurveyValidator validator;
    private final DiabetesSurveyService surveyService;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("survey/diabetes/style");
    }

    @ModelAttribute("requestDiabetesSurvey")
    public RequestDiabetesSurvey requestDiabetesSurvey() {
        RequestDiabetesSurvey form = new RequestDiabetesSurvey();
        form.setGender(Gender.FEMALE);
        form.setSmokingHistory(SmokingHistory.CURRENT);

        return form;
    }

    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    @ModelAttribute("smokingHistories")
    public SmokingHistory[] smokingHistories() {
        return SmokingHistory.values();
    }

    @GetMapping("/step1")
    public String step1(@ModelAttribute RequestDiabetesSurvey form, Model model) {
        commonProcess("step", model);

        return utils.tpl("survey/diabetes/step1");
    }

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
     * 설문 저장 및 결과 처리
     *
     * @param form
     * @param errors
     * @return
     */
    @PostMapping("/process")
    public String process(@Valid RequestDiabetesSurvey form, Errors errors, Model model, SessionStatus status) {
        commonProcess("step", model);

        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("survey/diabetes/step2");
        }

        // 설문 결과 및 저장 처리
        surveyService.process(form);

        // 처리 완료 후 세션값으로 더이상 변경되지 않도록 완료 처리
        status.setComplete();

        return "redirect:/survey/diabetes/result/설문번호";
    }


    /**
     * 설문 결과 보기
     *
     * @return
     */
    @GetMapping("/result/{seq}")
    public String result(@PathVariable("seq") Long seq, Model model) {
        commonProcess("result", model);

        return utils.tpl("survey/diabetes/result");
    }

    /**
     * 컨트롤러 공통 처리
     *
     * @param mode
     * @param model
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