package org.koreait.admin.product.services;

import lombok.RequiredArgsConstructor;
import org.koreait.product.constants.ProductStatus;
import org.koreait.product.entities.Product;
import org.koreait.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatusService {

    private final ProductRepository productRepository;

    /**
     * 선택된 상품 상태 일괄 변경
     */
    @Transactional
    public void updateStatus(List<Long> chks, String changeStatus) {
        if (chks == null || chks.isEmpty() || changeStatus == null || changeStatus.isBlank()) return;

        ProductStatus status;
        try {
            status = ProductStatus.valueOf(changeStatus);
        } catch (IllegalArgumentException e) {
            return; // 유효하지 않은 상태 코드일 경우 무시
        }

        List<Product> products = (List<Product>) productRepository.findAllById(chks);
        for (Product product : products) {
            product.setStatus(status);
        }

        productRepository.saveAll(products);
    }

    /**
     * 선택된 상품 일괄 삭제
     */
    public void deleteAllByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        productRepository.deleteAll(products);
    }
}

