package com.module3.ccafe.dto;

import com.module3.ccafe.entity.enums.CategoryStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Integer categoryId;
    String name;
    CategoryStatus status;
}