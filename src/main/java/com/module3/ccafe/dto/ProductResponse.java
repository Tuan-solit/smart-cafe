package com.module3.ccafe.dto;

import com.module3.ccafe.entity.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Integer productId;
    String name;
    BigDecimal price;
    String image;
    Integer categoryId;
    String categoryName;
    Integer sizeId;
    String sizeName;
    ProductStatus status;
    String statusDescription;
}
