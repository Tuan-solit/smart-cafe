package com.module3.ccafe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffUpdateRequest {
    private String fullName;
    private String phone;
    private String email;
}