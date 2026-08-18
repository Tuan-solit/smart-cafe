package com.module3.ccafe.dto;

import com.module3.ccafe.entity.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    Integer categoryId;
    Integer sizeId;
    BigDecimal price;
    MultipartFile image;
    ProductStatus status;
}