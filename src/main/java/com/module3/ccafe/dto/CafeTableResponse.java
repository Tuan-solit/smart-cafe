package com.module3.ccafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeTableResponse {
    private Integer tableId;
    private String tableNumber;
    private String urlQr;
    private String status;
}
