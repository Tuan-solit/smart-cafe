package com.module3.ccafe.dto.request;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashPaymentRequest {
    private Integer orderId;
    private String tableNumber;
    private String message;
    private LocalDateTime createdAt;
}
