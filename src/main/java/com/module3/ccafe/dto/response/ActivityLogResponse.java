package com.module3.ccafe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ActivityLogResponse {
    Integer logId;
    String action;
    LocalDateTime createdAt;
}