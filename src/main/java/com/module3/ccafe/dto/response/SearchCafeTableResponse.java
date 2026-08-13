package com.module3.ccafe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchCafeTableResponse {
    Integer tableId;
    String tableNumber;
    String urlQr;
    String status;
}
