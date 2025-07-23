package org.koreait.board.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final Utils utils;

    // 게시글 목록
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, Model model) {
        return utils.tpl("board/list");
    }
    // 게시글 작성
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid, Model model){

        return utils.tpl("board/write");
    }
    // 게시글 수정
    @GetMapping("/update/{seq}")
    public String update(@PathVariable("seq")Long seq, Model model){

        return utils.tpl("board/update");
    }
    // 게시글 보기
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {
        return utils.tpl("board/view");
    }
    // 게시글 삭제
    @GetMapping("/delete/{seq}")
    public String delete (@PathVariable("seq") Long seq, Model model){

        return "redirect:board/list";
    }

    private void commonProcess (String bid, String  mode, Model model){

    }

    private void commonProcess (String seq,  Model model){

    }
}

