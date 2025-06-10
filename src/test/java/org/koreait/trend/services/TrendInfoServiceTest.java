/*
package org.koreait.trend.services;

import org.junit.jupiter.api.Test;
import org.koreait.trend.entities.Trend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TrendInfoServiceTest {

    @Autowired
    private TrendInfoService infoService;

    private final String testUrl = "https://news.naver.com/"; //  테스트용 siteUrl (변경 가능)

    @Test
    void testTodayTrend() {
        Trend today = infoService.getTodayTrend(testUrl);
        assertThat(today).isNotNull();
        assertThat(today.getKeywords()).isNotBlank();
        assertThat(today.getWordCloud()).isNotBlank();
    }

    @Test
    void testWeeklyTrends() {
        List<Trend> weekly = infoService.getTrendsInRange(testUrl, 7);
        assertThat(weekly).isNotNull();
        assertThat(weekly.size()).isGreaterThan(0);
    }

    @Test
    void testMonthlyTrends() {
        List<Trend> monthly = infoService.getTrendsInRange(testUrl, 30);
        assertThat(monthly).isNotNull();
        assertThat(monthly.size()).isGreaterThan(0);
    }
}
*/
