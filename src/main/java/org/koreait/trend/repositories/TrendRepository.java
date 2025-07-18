package org.koreait.trend.repositories;

import com.querydsl.core.BooleanBuilder;
import org.koreait.trend.entities.QTrend;
import org.koreait.trend.entities.Trend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Order.desc;

public interface TrendRepository extends JpaRepository<Trend, Long>, QuerydslPredicateExecutor<Trend> {

    /**
     * 주어진 카테고리의 최신 트렌드를 가져옵니다.
     *
     * @param category : 조회할 트렌드의 카테고리
     * @return 최신 트렌드(Optional)
     */
    default Optional<Trend> getLatest(String category) {
        QTrend trend = QTrend.trend; // QueryDSL에서 사용할 QTrend 객체 생성
        Pageable pageable = PageRequest.of(0, 1, Sort.by(desc("createdAt"))); // 최신순으로 정렬하여 페이지 요청 생성
        Page<Trend> data = findAll(trend.category.eq(category), pageable); // 카테고리에 맞는 트렌드 조회
        List<Trend> items = data.getContent(); // 조회된 트렌드 목록 가져오기
        return Optional.ofNullable(items.getFirst()); // 첫 번째 요소를 Optional로 반환
    }

    /**
     * 주어진 카테고리와 날짜 범위에 해당하는 트렌드를 가져옵니다.
     *
     * @param category : 조회할 트렌드의 카테고리
     * @param sDate : 시작 날짜
     * @param eDate : 종료 날짜
     * @return 해당 범위의 트렌드(Optional)
     */
    default Optional<Trend> get(String category, LocalDateTime sDate, LocalDateTime eDate) {
        QTrend trend = QTrend.trend; // QueryDSL에서 사용할 QTrend 객체 생성
        BooleanBuilder andBuilder = new BooleanBuilder(); // 조건을 조합할 수 있는 BooleanBuilder 생성
        andBuilder.and(trend.category.eq(category)) // 카테고리 조건 추가
                .and(trend.createdAt.between(sDate, eDate)); // 날짜 범위 조건 추가

        Pageable pageable = PageRequest.of(0, 1, Sort.by(desc("createdAt"))); // 최신순으로 정렬하여 페이지 요청 생성

        Page<Trend> data = findAll(andBuilder, pageable); // 조건에 맞는 트렌드 조회
        List<Trend> items = data.getContent(); // 조회된 트렌드 목록 가져오기

        return Optional.ofNullable(items.getFirst()); // 첫 번째 요소를 Optional로 반환
    }

    /**
     * 주어진 카테고리와 날짜 범위에 해당하는 트렌드 목록을 가져옵니다.
     *
     * @param category : 조회할 트렌드의 카테고리
     * @param sDate : 시작 날짜
     * @param eDate : 종료 날짜
     * @return 해당 범위의 트렌드 목록
     */
    default List<Trend> getList(String category, LocalDateTime sDate, LocalDateTime eDate) {
        QTrend trend = QTrend.trend; // QueryDSL에서 사용할 QTrend 객체 생성
        BooleanBuilder andBuilder = new BooleanBuilder(); // 조건을 조합할 수 있는 BooleanBuilder 생성
        andBuilder.and(trend.category.eq(category)) // 카테고리 조건 추가
                .and(trend.createdAt.between(sDate, eDate)); // 날짜 범위 조건 추가

        return (List<Trend>) findAll(andBuilder); // 조건에 맞는 모든 트렌드 목록 반환
    }
}