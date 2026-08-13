package com.module3.ccafe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffRequest {
    private String fullName;
    private String phone;
    private String email;
    private String password;
}
