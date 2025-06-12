package org.koreait.restaurant.controllers;

import lombok.Data;
import org.koreait.global.search.CommonSearch;

@Data
public class RestaurantSearch extends CommonSearch {
    private double lat;
    private double lon;
    private int cnt;
}