package org.koreait.survey.diabetes.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.constants.Gender;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.survey.diabetes.constants.SmokingHistory;

@Data
@Entity
public class DiabetesSurvey extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int age;
    private boolean hypertension;

    private boolean heartDisease;

    @Enumerated(EnumType.STRING)
    private SmokingHistory smokingHistory;

    private double height;
    private double weight;
    private double bmi;

    private double hbA1c;

    private double bloodGlucoseLevel;

    private boolean diabetes;

    @ManyToOne(fetch=FetchType.LAZY)
    private Member member;
}