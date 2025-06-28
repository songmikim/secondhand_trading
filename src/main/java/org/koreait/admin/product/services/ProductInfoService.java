package org.koreait.admin.product.services;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.product.controllers.ProductSearch;
import org.koreait.global.search.ListData;
import org.koreait.global.search.Pagination;
import org.koreait.product.constants.ProductStatus;
import org.koreait.product.entities.Product;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final JdbcTemplate jdbcTemplate;

    public ListData<Product> getList(ProductSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = 10;
        int offset = (page - 1) * limit;

        List<Object> params = new ArrayList<>();
        List<String> addWhere = new ArrayList<>();

        String sopt = StringUtils.hasText(search.getSopt()) ? search.getSopt() : "ALL";
        String skey = search.getSkey();
        List<String> statusList = search.getStatusList();

        // 검색 조건 조립
        if (StringUtils.hasText(skey)) {
            if ("NAME".equalsIgnoreCase(sopt)) {
                addWhere.add("name LIKE ?");
                params.add("%" + skey + "%");
            } else if ("CATEGORY".equalsIgnoreCase(sopt)) {
                addWhere.add("category LIKE ?");
                params.add("%" + skey + "%");
            } else { // ALL
                addWhere.add("(name LIKE ? OR category LIKE ?)");
                params.add("%" + skey + "%");
                params.add("%" + skey + "%");
            }
        }

        if (statusList != null && !statusList.isEmpty()) {
            String placeholders = statusList.stream().map(s -> "?").collect(Collectors.joining(", "));
            addWhere.add("status IN (" + placeholders + ")");
            params.addAll(statusList);
        }

        String where = addWhere.isEmpty() ? "" : " WHERE " + String.join(" AND ", addWhere);

        // 목록 조회 쿼리
        String sql = "SELECT * FROM PRODUCT" + where + " ORDER BY createdAt DESC LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);

        List<Product> items = jdbcTemplate.query(sql, params.toArray(), this::productMapper);

        // 총 개수 조회 쿼리 (회원쪽 스타일 적용)
        String countSql = "SELECT COUNT(*) FROM PRODUCT" + where;
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, params.subList(0, params.size() - 2).toArray());

        Pagination pagination = new Pagination(page, total, 10, limit);
        return new ListData<>(items, pagination);
    }

    private Product productMapper(ResultSet rs, int i) throws SQLException {
        Product product = new Product();
        product.setSeq(rs.getLong("seq"));
        product.setGid(rs.getString("gid"));
        product.setName(rs.getString("name"));
        product.setCategory(rs.getString("category"));
        product.setConsumerPrice(rs.getInt("consumerPrice"));
        product.setSalePrice(rs.getInt("salePrice"));
        product.setDescription(rs.getString("description"));

        String statusStr = rs.getString("status");
        if (StringUtils.hasText(statusStr)) {
            product.setStatus(ProductStatus.valueOf(statusStr));
        }

        product.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        return product;
    }

}
