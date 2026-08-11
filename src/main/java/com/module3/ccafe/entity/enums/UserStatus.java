package com.module3.ccafe.entity.enums;

public enum UserStatus {
    ACTIVE ("Hoạt động"),
    INACTIVE("Không hoạt động");

    private String description;


    UserStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }


}
