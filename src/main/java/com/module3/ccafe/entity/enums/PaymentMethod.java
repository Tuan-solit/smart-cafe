package com.module3.ccafe.entity.enums;

public enum PaymentMethod {
    ONLINE("Trực tuyến"),
    CASH("Tiền mặt");

    private String description;

    public String getDescription() {
        return description;
    }

    PaymentMethod(String description) {
        this.description = description;
    }
}
