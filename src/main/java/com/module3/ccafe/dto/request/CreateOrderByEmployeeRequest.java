package com.module3.ccafe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderByEmployeeRequest {
    Integer idBan;
    Integer idEmployee;
    LocalDateTime startTime;

}
