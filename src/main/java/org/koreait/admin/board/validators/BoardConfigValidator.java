package org.koreait.admin.board.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.board.controllers.RequestBoard;
import org.koreait.board.repositories.BoardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class BoardConfigValidator implements Validator {

    private final BoardRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }

        RequestBoard form = (RequestBoard) target;
        String mode = form.getMode();
        String bid = form.getBid();

        // 게시판 등록시 게시판 아이디가(bid) 이미 있으면 등록 불가
        if(mode.equals("register") && repository.existsByBid(bid)){
            errors.rejectValue("bid", "Duplicated");

        }
    }
}
