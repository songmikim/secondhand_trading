package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.koreait.member.controllers.RequestLogin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    /*
    * AuthenticationException exception
    * - 인증 실패시에 발생하는 예외
    * - 예외는 상황에 따라서 다양한 예외 클래스
    * */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession();
        RequestLogin form = (RequestLogin) session.getAttribute("requestLogin");
        form = Objects.requireNonNullElseGet(form, RequestLogin::new);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        List<String > fieldErrors = new ArrayList<>();
        List<String > globalErrors = new ArrayList<>();

        form.setEmail(email);
        form.setPassword(password);
        form.setFieldErrors(fieldErrors);
        form.setGlobalErrors(globalErrors);

        /*
        * 1. 이메일, 비밀번호가 누락된경우
        * 2. 이메일이 다른 경우
        * 3. 비밀번호가 다른 경우
        * */
        if(exception instanceof BadCredentialsException){
            if (!StringUtils.hasText(email)){
                fieldErrors.add("email_NotBlank");
            }

            if(!StringUtils.hasText(password)){
                fieldErrors.add("password_NotBlank");
            }

            if(fieldErrors.isEmpty()){ // 필수 항목은 다 있지만 이메일 또는 비밀번호가 불일치
                globalErrors.add("Authentication.bad.credential");
            }
        }

        session.setAttribute("requestLogin", form);
        // 로그인 실패시에는 로그인 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/member/login");
    }
}
