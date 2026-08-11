package com.module3.ccafe.entity.enums;

public enum PaymentStatus
{
    PENDING("Chờ"),
    SUCCESS("Thành công"),
    FAILED("Thất bại");


    private String description;

    public String getDescription() {
        return description;
    }

    PaymentStatus(String description) {
        this.description = description;
    }
}
