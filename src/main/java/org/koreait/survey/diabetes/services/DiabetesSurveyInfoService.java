package org.koreait.survey.diabetes.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.constants.Gender;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.koreait.global.search.CommonSearch;
import org.koreait.global.search.ListData;
import org.koreait.global.search.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.koreait.survey.exceptions.SurveyNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class DiabetesSurveyInfoService {

    private final HttpServletRequest request;
    private final JdbcTemplate jdbcTemplate;
    private final MemberUtil memberUtil;

    /**
     * 설문지 한개 조회
     * - 본인이 작성한 설문지만 조회
     * - 현재 로그인 회원이 관리자이면 상관없이 조회 가능
     *
     * @param seq
     * @return
     */
    public DiabetesSurvey get(Long seq) {
        try {
            String sql = "SELECT s.*, m.email, m.name, m.mobile FROM SURVEY_DIABETES s " +
                    " LEFT JOIN MEMBER m ON s.memberSeq = m.seq WHERE s.seq = ?";
            DiabetesSurvey item = jdbcTemplate.queryForObject(sql, this::mapper, seq);

            Member member = memberUtil.getMember(); // 로그인한 회원 정보
            if (!memberUtil.isLogin() || (!memberUtil.isAdmin() && !member.getSeq().equals(item.getMemberSeq()))) { // 로그인 상태가 아니거나, 관리자가 아닌 회원 로그인일때 설문지 작성 회원과 일치 하지 않다면
                throw new UnAuthorizedException();
            }


            return item;
        } catch (DataAccessException e) { // 조회가 안된 경우
            throw new SurveyNotFoundException();
        }
    }

    /**
     * 목록 조회
     * - 회원 전용
     *
     * @param search
     * @return
     */
    public ListData<DiabetesSurvey> getList(CommonSearch search) {
        if (!memberUtil.isLogin()) {
            return new ListData<>();
        }

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit; // 레코드 시작 번호

        Member member = memberUtil.getMember(); // 현재 로그인한 회원 정보

        String sql = "SELECT s.*, m.email, m.name, m.mobile FROM SURVEY_DIABETES s " +
                " LEFT JOIN MEMBER m ON s.memberSeq = m.seq WHERE memberSeq = ? " +
                " ORDER BY s.createdAt DESC LIMIT ?, ?";

        List<DiabetesSurvey> items = jdbcTemplate.query(sql, this::mapper, member.getSeq(), offset, limit);

        int total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SURVEY_DIABETES WHERE memberSeq = ?", int.class, member.getSeq());

        // Pagination(int page, int total, int range, int limit, HttpServletRequest request)
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    private DiabetesSurvey mapper(ResultSet rs, int i) throws SQLException {
        DiabetesSurvey item = new DiabetesSurvey();
        item.setSeq(rs.getLong("seq"));
        item.setMemberSeq(rs.getLong("memberSeq"));
        item.setGender(Gender.valueOf(rs.getString("gender")));
        item.setAge(rs.getInt("age"));
        item.setDiabetes(rs.getBoolean("diabetes"));
        item.setBmi(rs.getDouble("bmi"));
        item.setHeight(rs.getDouble("height"));
        item.setWeight(rs.getDouble("weight"));
        item.setHypertension(rs.getBoolean("hypertension"));
        item.setHeartDisease(rs.getBoolean("heartDisease"));
        item.setHbA1c(rs.getDouble("hbA1c"));
        item.setBloodGlucoseLevel(rs.getDouble("bloodGlucoseLevel"));
        item.setSmokingHistory(SmokingHistory.valueOf(rs.getString("smokingHistory")));

        Member member = new Member();
        member.setSeq(rs.getLong("memberSeq"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setMobile(rs.getString("mobile"));

        item.setMember(member);

        return item;
    }
}