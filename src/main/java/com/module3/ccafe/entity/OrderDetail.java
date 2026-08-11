package com.module3.ccafe.entity;


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
public class OrderDetail {
    @EmbeddedId
    OrderDetailId orderDetailId;

    Integer quantity;

    BigDecimal unitPrice;

    String note;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "id")
    Order order;


    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "id")
    Product product;
}
