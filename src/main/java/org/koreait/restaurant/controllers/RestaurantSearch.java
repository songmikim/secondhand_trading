package org.koreait.restaurant.controllers;

import lombok.Data;
import org.koreait.global.search.CommonSearch;

@Data
public class RestaurantSearch extends CommonSearch {
    private double lat; // 위도
    private double lon; // 경도
    private int cnt;
}