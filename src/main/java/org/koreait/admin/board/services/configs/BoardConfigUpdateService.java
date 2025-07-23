package org.koreait.admin.board.services.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.board.controllers.RequestBoard;
import org.koreait.board.repositories.BoardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigUpdateService {
    private final BoardRepository boardRepository;

    public void process(RequestBoard form){
        String bid = form.getBid();
        String mode = Objects.requireNonNullElse(form.getMode(), "register");
    }
}
