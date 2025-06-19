package org.koreait.survey.diabetes.services;

import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.koreait.survey.diabetes.repositories.DiabetesSurveyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class DiabetesSurveyService {

    private final DiabetesSurveyPredictService predictService;
    private final DiabetesSurveyRepository repository;
    private final MemberUtil memberUtil;
    private final ModelMapper mapper;

    public DiabetesSurvey process(RequestDiabetesSurvey form) {
        /**
         * 1. 설문 답변으로 당뇨 고위험군 예측 결과 가져오기
         * 2. 로그인한 회원 정보 가져오기
         * 3. DB에 저장 처리
         */

        boolean diabetes = predictService.isDiabetes(form);
        Member member = memberUtil.getMember();
        double bmi = predictService.getBmi(form.getHeight(), form.getWeight());

        DiabetesSurvey item = mapper.map(form, DiabetesSurvey.class);

        item.setDiabetes(diabetes);
        item.setBmi(bmi);
        if(memberUtil.isLogin()){
            item.setMemberSeq(memberUtil.getMember().getSeq());
        }

        repository.save(item);

        return repository.findById(item.getSeq()).orElse(null);
    }
}