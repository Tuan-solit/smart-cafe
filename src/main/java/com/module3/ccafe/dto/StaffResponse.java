package com.module3.ccafe.dto;

import com.module3.ccafe.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffResponse {
    private Integer userId;
    private String fullName;
    private String phone;
    private String email;
    private Integer roleId;
    private String roleName;
    private UserStatus status;
}
