package com.module3.ccafe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SepayRequest {
    String subAccount;
    String code;
    String content;
    String transferType;
    String description;
    Integer transferAmount;
    String referenceCode;
    Integer accumulated;
    Integer id;
}
