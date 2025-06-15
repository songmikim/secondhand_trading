package org.koreait.trend.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("TREND_URL")
public class TrendUrl {
    @Id
    @Column("siteUrl")
    private String siteUrl;
}
