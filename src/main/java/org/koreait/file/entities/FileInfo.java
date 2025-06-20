package org.koreait.file.entities;

import lombok.Data;
import org.koreait.global.entities.BaseEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("FILE_INFO")
public class FileInfo extends BaseEntity {
    @Id
    private Long seq;
    private String gid;
    private String location;

    @Column("fileName")
    private String fileName;
    private String extension; // 확장자

    @Column("contentType")
    private String contentType; // 파일 종류

    @CreatedBy
    @Column("createdBy")
    private String createdBy; // 업로드한 로그인 사용자의 이메일

    @Transient // 엔티티 내부에서 사용할 목적의 필드임을 알려주는 애노테이션
    private String filePath; // 파일이 위치한 서버 경로

    @Transient
    private String fileUrl; // 브라우저에서 접근 가능한 URL

    @Transient
    private String thumbBaseUrl;

    @Transient
    private String thumbBasePath;

    @Transient
    private boolean image;
}