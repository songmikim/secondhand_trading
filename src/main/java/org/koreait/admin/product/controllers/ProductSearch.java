package org.koreait.admin.product.controllers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.koreait.global.search.CommonSearch;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSearch extends CommonSearch {
    private List<String> statusList;            // 상품 상태: SALE, SOLD_OUT
}