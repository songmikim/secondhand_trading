package org.koreait.survey.diabetes.repositories;

import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DiabetesSurveyRepository extends JpaRepository<DiabetesSurvey, Long>, QuerydslPredicateExecutor<DiabetesSurvey> {

}