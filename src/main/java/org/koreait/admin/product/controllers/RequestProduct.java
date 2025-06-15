package org.koreait.admin.product.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.koreait.product.constants.ProductStatus;

@Data
public class RequestProduct {
    private String mode; // add - 추가, edit -  수정
    private Long seq;
    private String gid;

    @NotBlank
    private String name;
    private String category;

    @NotNull
    private ProductStatus status;
    private int consumerPrice;
    private int salePrice;

    private String description;
}
