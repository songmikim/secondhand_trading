package org.koreait.board.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.file.entities.FileInfo;

import java.util.List;

@Data
public class RequestBoard {
    private String mode;
    private Long seq;

    @NotBlank
    private String bid;

    @NotBlank
    private String gid;

    @NotBlank
    private String poster;
    private String guestPw;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
    private boolean notice; // 공지글 여부
    private boolean secret; // 비밀글 여부
    private boolean afterWritingRedirect;  // 글 작성 후 이동 방향 - false : 게시글 목록, true : 게시글 상세
    private boolean guest; // 비회원 게시글 작성, 수정 여부

    private List<FileInfo> editorImages;
    private List<FileInfo> attachFiles;

    private String category;
}