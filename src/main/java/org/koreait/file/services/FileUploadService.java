package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.koreait.global.exceptions.script.AlertBackException;
import org.koreait.global.libs.Utils;
import org.koreait.member.libs.MemberUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileUploadService {
    private final Utils utils;
    private final FileProperties properties;
    private final FileInfoRepository repository;
    private final MemberUtil memberUtil;

    public List<FileInfo> process(RequestUpload form){
        String gid = form.getGid(); // 그룹ID
        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString();
        String  location = form.getLocation();

        MultipartFile[] files = form.getFiles();
        if(files == null) {
            throw new AlertBackException(utils.getMessage("NotUpload.file"), HttpStatus.BAD_REQUEST);
        }

        String basePath = properties.getPath(); // 파일 업로드 기본 경로

        List<FileInfo> uploadedFiles = new ArrayList<>();
        for(MultipartFile file : files) {

            String fileName = file.getOriginalFilename();   // 업로드할때 파일명
            String extension = fileName.substring(fileName.lastIndexOf("."));   // 확장자
            String contentType = file.getContentType();

            FileInfo item = new FileInfo();
            item.setGid(gid);
            if(StringUtils.hasText(location)){
                item.setLocation(location);
            }

            item.setFileName(fileName);
            item.setExtension(extension);
            item.setContentType(contentType);

            repository.save(item);
            // 1. 업로드한 파일 정보를 DB에 기록 E

            // 2. seq, extension으로 서버에 올릴 경로를 만들어주고 업로드 처리
            long seq = item.getSeq();
            long folder = seq % 10L; // 0~9
            String _fileName = seq + Objects.requireNonNullElse(extension, "");
            String uploadDir = String.format("%s/%s", basePath, folder);
            File _uploadDir = new File(uploadDir);
            if(!_uploadDir.exists() || !_uploadDir.isDirectory()){
                _uploadDir.mkdir();
            }

            File uploadPath = new File(_uploadDir, _fileName);
            try{
                file.transferTo(uploadPath);
                uploadedFiles.add(item);
            } catch (IOException e){
                // 업로드 실패시 저장된 DB 데이터 삭제
                repository.deleteById(seq);
                e.printStackTrace();
            }
        }
        return uploadedFiles;
    }
}
