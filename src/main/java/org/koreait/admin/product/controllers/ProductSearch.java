package org.koreait.admin.product.controllers;

import lombok.Data;
import org.koreait.global.search.CommonSearch;

@Data
public class ProductSearch extends CommonSearch {
    private String status;              // 상품 상태: SALE, SOLD_OUT
}