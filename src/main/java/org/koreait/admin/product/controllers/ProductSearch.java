package org.koreait.admin.product.controllers;

import lombok.Data;

@Data
public class ProductSearch {
    private String sopt = "ALL";        // 검색옵션: NAME, CATEGORY, ALL
    private String skey;                // 검색어
    private String status;              // 상품 상태: SALE, SOLD_OUT

    private int page = 1;               // 페이지 번호
}