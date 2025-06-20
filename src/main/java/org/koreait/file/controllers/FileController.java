package org.koreait.file.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileUploadService uploadService;
    private final FileDeleteService deleteService;
    private final FileInfoService infoService;
    private final FileDownloadService downloadService;
    private final ThumbnailService thumbnailService;

    @PostMapping("/upload")
    public List<FileInfo> upload(RequestUpload form, @RequestPart("file") MultipartFile[] files) {
        form.setFiles(files);
        List<FileInfo> items = uploadService.process(form);

        return items;
    }

    @GetMapping({"/list/{gid}", "/list/{gid}/{location}"})
    public List<FileInfo> list(@PathVariable("gid") String gid, @PathVariable(name="location", required = false) String location) {

        List<FileInfo> items = infoService.getList(gid, location);

        return items;
    }

    @GetMapping("/info/{seq}")
    public FileInfo info(Long seq) {
        FileInfo item = infoService.get(seq);

        return item;
    }

    @DeleteMapping("/delete/{seq}")
    public FileInfo delete(Long seq) {
        FileInfo item = deleteService.process(seq);

        return item;
    }

    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public List<FileInfo> deletes(@PathVariable("gid") String gid, @PathVariable(name="location", required = false) String location) {
        List<FileInfo> items = deleteService.process(gid, location);

        return items;
    }
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
    public void thumb(RequestThumb form, HttpServletResponse response) {
        String path = thumbnailService.create(form);
        if (!StringUtils.hasText(path)) {
            return;
        }

        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            String contentType = Files.probeContentType(file.toPath()); // 이미지 파일 형식
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