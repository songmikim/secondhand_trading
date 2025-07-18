package org.koreait.survey.diabetes.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.koreait.global.search.CommonSearch;
import org.koreait.global.search.ListData;
import org.koreait.global.search.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.koreait.survey.diabetes.entities.QDiabetesSurvey;
import org.koreait.survey.diabetes.repositories.DiabetesSurveyRepository;
import org.koreait.survey.exceptions.SurveyNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Lazy
@Service
@Transactional
@RequiredArgsConstructor
public class DiabetesSurveyInfoService {

    private final HttpServletRequest request;
    private final DiabetesSurveyRepository repository;
    private final JPAQueryFactory queryFactory;
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
            DiabetesSurvey item = repository.findById(seq).orElseThrow(SurveyNotFoundException::new);

            Member member = item.getMember();
            Member loggedMember = memberUtil.getMember(); // 로그인한 회원 정보
            if (!memberUtil.isLogin() || (!memberUtil.isAdmin() && !loggedMember.getSeq().equals(member.getSeq()))) { // 로그인 상태가 아니거나, 관리자가 아닌 회원 로그인일때 설문지 작성 회원과 일치 하지 않다면
                throw new UnAuthorizedException();
            }

            return item;

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

        Member loggedMember = memberUtil.getMember(); // 현재 로그인한 회원 정보
        QDiabetesSurvey diabetesSurvey = QDiabetesSurvey.diabetesSurvey;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(diabetesSurvey.member.eq(loggedMember));

        List<DiabetesSurvey> items = queryFactory.selectFrom(diabetesSurvey)
                .leftJoin(diabetesSurvey.member)
                .fetchJoin()
                .where(andBuilder)
                .orderBy(diabetesSurvey.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        long total = repository.count(andBuilder);

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }
}