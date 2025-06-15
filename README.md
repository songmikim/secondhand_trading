# 프로젝트 소개
- 트렌드 분석 관련 내용 및 목적


# 역할 분담
- 조원 이름, 작업 내용

# 기능 설명
- 설명
- 중요한 코드가 있으면 코드와 함께 설명
- 구현화면에 대한 이미지


-------------------------------
# todo


1. 테이블 생성 : siteUrl
2. 파이썬파일 추가 : etc_trend.py
# ALTER TABLE TREND ADD COLUMN siteUrl VARCHAR(1024) AFTER category;


TrendInfoService

--------------------------------------


## NewsTrendService 기능 요약
  - python 가상환경 활성화
  - 특정 파이썬 스크립트 실행 (trend.py)
  - 결과(JSON)를 NewsTrend로 역직렬화
  - 결과 이미지를 특정 경로에 저장하고, DB에 저장
  - 매 시간마다 실행하는 @Scheduled 작업 포함

-------------------------------------------------------

@Valid TrendSearch에서 siteUrl이 null인 상태로 요청되어
Spring의 유효성 검사 실패 → 500 INTERNAL_SERVER_ERROR

@NotBlank는 값이 없을 경우 무조건 오류 발생
→ 처음 폼 진입 시 siteUrl이 비어있어서 검증 실패