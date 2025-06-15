package org.koreait.restaurant.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("RESTAURANT")
public class Restaurant {
    @Id
    private Long seq;
    private String zipcode;
    private String address;
    private String zonecode;

    @Column("roadAddress")
    private String roadAddress;
    private String category;
    private String name;
    private double lat;
    private double lon;
}
