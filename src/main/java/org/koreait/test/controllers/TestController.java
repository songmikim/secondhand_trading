package org.koreait.test.controllers;

import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.exceptions.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@ApplyCommonController
public class TestController {
    @GetMapping
    public String test1(){
        boolean result = false;
        if(!result){
            throw new BadRequestException("테스트 예외 발생!");
        }
        return "test";
    }
}
