package org.koreait.survey.diabetes.entities;

import lombok.Data;
import org.koreait.global.constants.Gender;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("SURVEY_DIABETES")
public class DiabetesSurvey extends BaseEntity {
    @Id
    private Long seq;

    @Column("memberSeq")
    private Long memberSeq;

    private Gender gender;
    private int age;
    private boolean hypertension;

    @Column("heartDisease")
    private boolean heartDisease;

    @Column("smokingHistory")
    private SmokingHistory smokingHistory;

    private double height;
    private double weight;
    private double bmi;

    @Column("hbA1c")
    private double hbA1c;

    @Column("bloodGlucoseLevel")
    private double bloodGlucoseLevel;

    private boolean diabetes;

    @Transient
    private Member member;
}