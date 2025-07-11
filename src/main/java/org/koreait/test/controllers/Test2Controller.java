package org.koreait.test.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test2")
@RequiredArgsConstructor
public class Test2Controller {
    private final Utils utils;

    @GetMapping
    public void test1() {
        boolean result = false;
        if (!result) {
            throw new BadRequestException("REST 예외 발생!");
        }
    }

    @PostMapping
    public void test2(@Valid @RequestBody RequestTest form, Errors errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }
    }
}