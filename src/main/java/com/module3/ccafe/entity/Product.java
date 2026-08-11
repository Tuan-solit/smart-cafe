package com.module3.ccafe.entity;

import com.module3.ccafe.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    Long id;
    String name;
    BigDecimal price;
    String urlPicture;

    @Enumerated(EnumType.STRING)
    ProductStatus productStatus;

    @ManyToOne
    @JoinColumn(name = "id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "id")
    Size size;
}
