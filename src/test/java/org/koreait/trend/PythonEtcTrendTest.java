package org.koreait.trend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;

@SpringBootTest
public class PythonEtcTrendTest {

    @Test
    void testEtcPythonExecution() throws Exception {
        // 1. 가상환경 활성화 (Windows 기준)
        ProcessBuilder builder = new ProcessBuilder("C:/trend/.venv/Scripts/activate.bat");
        Process process = builder.start();
        int statusCode = process.waitFor();
        if (statusCode != 0) {
            System.out.println("가상환경 활성화 실패");
            return;
        }

        // 2. etc_trend.py 실행
        builder = new ProcessBuilder(
                "C:/trend/.venv/Scripts/python.exe",
                "C:/trend/etc_trend.py",             // 파이썬 파일 경로
                "C:/uploads/trend",                  // 이미지 저장 경로
                "https://news.naver.com/"            // 테스트할 siteUrl
        );
        process = builder.start();
        statusCode = process.waitFor();

        // 3. 결과 확인
        if (statusCode == 0) {
            System.out.println("=== 파이썬 실행 결과 ===");
            process.inputReader().lines().forEach(System.out::println);
        } else {
            System.out.println("=== 파이썬 에러 ===");
            BufferedReader br = process.errorReader();
            br.lines().forEach(System.out::println);
        }
    }
}