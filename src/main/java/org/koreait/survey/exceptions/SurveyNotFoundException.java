package org.koreait.survey.exceptions;

import org.koreait.global.exceptions.script.AlertBackException;
import org.springframework.http.HttpStatus;

/**
 * 설문 정보를 찾을 수 없을 때 발생시키는 예외 클래스
 */
public class SurveyNotFoundException extends AlertBackException {
    /**
     * 예외 생성자: 메시지 코드와 HTTP 상태를 지정하고 errorCode를 활성화
     */
    public SurveyNotFoundException(){
        // 메시지 리소스에서 'NotFound.survey' 코드를 조회하여 알림 메시지로 사용
        // HTTP 상태 코드를 404 Not Found로 설정
        super("NotFound.survey", HttpStatus.NOT_FOUND);
        // errorCode를 true로 설정하여 클라이언트에서 에러 코드를 확인할 수 있도록 함
        setErrorCode(true);
    }
}
