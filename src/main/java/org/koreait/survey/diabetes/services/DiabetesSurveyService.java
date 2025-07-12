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

    /**
     * 설문 폼을 기반으로 당뇨 위험 결과를 예측하고, 회원 정보와 함께 DB에 저장
     *
     * @param form 사용자 입력 설문 폼(RequestDiabetesSurvey)
     * @return 저장된 DiabetesSurvey 엔티티 (저장 실패 시 null)
     */
    public DiabetesSurvey process(RequestDiabetesSurvey form) {
        // 1. 예측 서비스로부터 당뇨 위험 여부 얻기
        boolean diabetes = predictService.isDiabetes(form);
        // 2. 로그인된 회원 정보 조회
        Member member = memberUtil.getMember();
        // 3. BMI 재계산 (예측 시와 동일하게 적용)
        double bmi = predictService.getBmi(form.getHeight(), form.getWeight());
        // 4. 폼 데이터를 엔티티로 매핑
        DiabetesSurvey item = mapper.map(form, DiabetesSurvey.class);
        // 5. 예측 결과 및 BMI 설정
        item.setDiabetes(diabetes);
        item.setBmi(bmi);
        // 6. 로그인 상태면 회원 시퀀스 설정
        if(memberUtil.isLogin()){
            item.setMemberSeq(memberUtil.getMember().getSeq());
        }
        // 7. DB에 저장
        repository.save(item);
        // 8. 저장된 엔티티를 다시 조회하여 반환
        return repository.findById(item.getSeq()).orElse(null);
    }
}