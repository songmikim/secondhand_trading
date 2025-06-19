package org.koreait.file.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileDownloadService;
import org.koreait.file.services.ThumbnailService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileDownloadService downloadService;
    private final ThumbnailService thumbnailService;

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

    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response){
        String path = thumbnailService.create(form);
        if(!StringUtils.hasText(path)){

            return;
        }

        File file = new File(path);
        try (
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis)){
            String  contentType = Files.probeContentType(file.toPath()); // 이미지 파일 형식
                response.setContentType(contentType);

                OutputStream out = response.getOutputStream();
                out.write(bis.readAllBytes());
            } catch (IOException e) {}

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