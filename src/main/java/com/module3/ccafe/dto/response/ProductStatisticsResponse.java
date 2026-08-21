package com.module3.ccafe.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatisticsResponse {

    private Integer productId;

    private String productName;

    private Long totalQuantity;

    private BigDecimal totalRevenue;
}