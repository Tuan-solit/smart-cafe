package com.module3.ccafe.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentHistoryResponse {

    private Integer paymentId;

    private Integer orderId;

    private String tableNumber;

    private String employeeName;

    private BigDecimal total;

    private String paymentMethod;

    private String internalTransactionCode;

    private String gatewayTransactionCode;

    private String status;

    private String statusDescription;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;
}