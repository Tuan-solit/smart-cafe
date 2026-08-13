package com.module3.ccafe.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterResponse {
    String fullName;
    String email;
    String phone;
}
