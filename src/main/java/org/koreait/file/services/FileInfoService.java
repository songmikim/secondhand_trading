package org.koreait.file.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {
    private final FileInfoRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final HttpServletRequest request;
    private final FileProperties properties;

    /**
     * 파일 한개 조회
     *
     * @param seq : 파일 등록번호
     * @return
     */
    public FileInfo get(Long seq) {
        FileInfo item = repository.findById(seq).orElseThrow(FileNotFoundException::new);

        // 추가정보 공통 처리
        addInfo(item);

        return item;
    }

    /**
     * 파일 목록 조회
     *
     * @param gid : 그룹 ID
     * @param location : 그룹 내에서 구분 위치값
     * @return
     */
    public List<FileInfo> getList(String gid, String location) {
        List<Object> params = new ArrayList<>();
        StringBuffer sb = new StringBuffer(2000);
        sb.append("SELECT * FROM FILE_INFO WHERE gid=?");
        params.add(gid);

        if (StringUtils.hasText(location)) {
            sb.append(" AND location=?");
            params.add(location);
        }

        sb.append(" ORDER BY createdAt DESC");
        List<FileInfo> items = jdbcTemplate.query(sb.toString(), this::mapper, params.toArray());

        // 추가정보공통 처리
        items.forEach(this::addInfo);

        return items;
    }

    private FileInfo mapper(ResultSet rs, int i) throws SQLException {
        FileInfo item = new FileInfo();
        item.setSeq(rs.getLong("seq"));
        item.setGid(rs.getString("gid"));
        item.setLocation(rs.getString("location"));
        item.setFileName(rs.getString("fileName"));
        item.setContentType(rs.getString("contentType"));
        item.setCreatedBy(rs.getString("createdBy"));
        item.setExtension(rs.getString("extension"));
        item.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

        return item;

    }

    /**
     * 추가 정보 처리
     * 1) 파일이 위치하고 있는 서버쪽 경로
     * 2) 브라우저에서 접근 가능한 URL
     * 3) 이미지인 경우 썸네일 이미지 URL
     * @param item
     */
    private void addInfo(FileInfo item) {
        item.setFileUrl(getFileUrl(item));
        item.setFilePath(getFilePath(item));

        /* 파일이 이미지인지 체크 */
        String contentType = item.getContentType();
        item.setImage(StringUtils.hasText(contentType) && contentType.startsWith("image"));

        /* 이미지인 경우 썸네일 기본 URL, 기본 Path  추가 */
        if (item.isImage()) {
            String folder = folder(item);
            String thumbPath = String.format("%s/thumbs/%s/", properties.getPath(), folder);
            String thumbUrl = String.format("%s/%s/thumbs/%s/", request.getContextPath(), properties.getUrl(), folder);
            item.setThumbBasePath(thumbPath);
            item.setThumbBaseUrl(thumbUrl);
        }
    }

    public String folder(FileInfo item) {
        long seq = item.getSeq();

        return folder(seq);
    }

    public String folder(long seq) {
        return String.valueOf(seq % 10L); // 0 ~ 9
    }

    // 브라우저에서 접근할 수 있는 URL
    public String getFileUrl(FileInfo item) {
        return String.format("%s%s/%s/%s", request.getContextPath(), properties.getUrl(), folder(item), item.getSeq() + Objects.requireNonNullElse(item.getExtension(), ""));
    }

    // 파일이 위치한 서버 경로
    public String getFilePath(FileInfo item) {
        return String.format("%s/%s/%s", properties.getPath(), folder(item), item.getSeq() + Objects.requireNonNullElse(item.getExtension(), ""));
    }
}