package com.module3.ccafe.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @NotBlank(message = "Họ tên không được để trống")
    String fullName;

    @Pattern(regexp = "^[0-9]{9,11}", message = "Số điện thoại không hợp lệ")
    String phone;

    @Email(message = "Email không hợp lệ")
    String email;

}
