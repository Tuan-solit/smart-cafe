package com.module3.ccafe.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Integer productId;
    
    private String productName;

    private String image;

    private BigDecimal price;

    private Integer quantity;

    private String note;

    public BigDecimal getSubTotal() {
        return price.multiply(
                BigDecimal.valueOf(quantity)
        );
    }
}
