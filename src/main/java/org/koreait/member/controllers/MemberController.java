package org.koreait.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.member.services.JoinService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@SessionAttributes("requestLogin")
public class MemberController {

    private final Utils utils;
    private final JoinValidator joinValidator;
    private final JoinService joinService;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("member/style");
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin(){
        return new RequestLogin();
    }
    // 회원가입 양식
    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form, Model model) {
        commonProcess("join", model);

        return utils.tpl("member/join");
    }

    // 회원가입 처리
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED) // 응답 코드 201
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model) {
        commonProcess("join", model);

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("member/join");
        }

        joinService.process(form);

        // 회원가입 성공시
        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model);

        /* 검증 실패 처리 S*/
        List<String > fieldErrors = form.getFieldErrors();
        if (fieldErrors != null){
           fieldErrors.forEach( s -> {
               // 0 - 필드, 1 - 에러코드
               String[] value = s.split("_");
               errors.rejectValue(value[0], value[1]);
           });
        }
        List<String> globalErrors = form.getGlobalErrors();
        if(globalErrors != null) {
            globalErrors.forEach(errors::reject);
        }
        /* 검증 실패 처리 E*/
        return utils.tpl("member/login");
    }

    /**
     * 현재 컨트롤러의 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "join";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("join")) { // 회원 가입 공통 처리
            addCommonScript.add("fileManager");
            addScript.add("member/join");
            pageTitle = utils.getMessage("회원가입");

        } else if (mode.equals("login")) { // 로그인 공통 처리
            pageTitle = utils.getMessage("로그인");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
    }
}