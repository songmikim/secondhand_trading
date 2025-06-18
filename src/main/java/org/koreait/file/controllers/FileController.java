package org.koreait.file.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileDownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileDownloadService downloadService;

    /**
     * 파일 다운로드
     *
     *
     *
     */
    @GetMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq) {
        downloadService.process(seq);
    }

//    @GetMapping("/download/{seq}")
//    public void download(@PathVariable("seq") Long seq) {
//        response.setHeader("Content-Disposition", "attachment; filename=test.txt");
//
//        try {
//            PrintWriter out = response.getWriter();
//            out.println("test1");
//            out.println("test2");
//            out.println("test3");
//
//        } catch (IOException e) {}
//    }
}