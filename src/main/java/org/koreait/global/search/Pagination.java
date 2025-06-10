package org.koreait.global.search;

import groovy.transform.ToString;


@ToString
public class Pagination{
    private int page;
    private int total;
    private int range;
    private int limit;

    /*
     * @param page : 페잊 번호, 1,2,3,4
     * @param total : 총 레코드 갯수
     * @param range : 페이지 구간
     * @param limit : 한 페이지당 보여줄 레코드 갯수
     * */
    public Pagination(int page, int total, int range, int limit){
        page = Math.max(page,1);
        range = range < 1 ? 10 : range;
        limit = limit < 1 ? 20 : limit;

        if (total < 1) return;

        // 전체 페이지 갯수
        int totalPages = (int)Math.ceil(total / (double)limit);

        // 페이지 구간 번호 - 0, 1, 2 ...
        int rangeNum = (page -1) / range;
        int firstRangePage = rangeNum * range + 1;  // 현재 구간에서의 첫번째 페이지
        int lastRangePage = firstRangePage + range -1 ; // 현재 구간에서의 마지막 페이지 번호
        lastRangePage = Math.min(totalPages, lastRangePage); // 마지막 페이지는 총 페이지의 갯수 이하

        // 이전 구간의 시작 페이지
        int prevRangePage = 0; // 0이면 이전 구간이 없다.
        if (rangeNum > 0){  // 이전 구간이 있는 경우
            prevRangePage = firstRangePage;
        }
    }
}
