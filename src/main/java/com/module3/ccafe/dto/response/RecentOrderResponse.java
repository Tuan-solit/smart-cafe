package com.module3.ccafe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecentOrderResponse {
    Integer orderId;
    String tableNumber;
    String status;
    BigDecimal total;
}
