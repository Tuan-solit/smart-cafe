package com.module3.ccafe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailViewResponse {
    Integer orderId;
    String tableNumber;
    String status;
    LocalDateTime createdAt;
    List<OrderItemResponse> items;
    BigDecimal total;
}
