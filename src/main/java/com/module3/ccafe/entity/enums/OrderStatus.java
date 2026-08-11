package com.module3.ccafe.entity.enums;

public enum OrderStatus {
    OPEN("Đang mở"),
    PAID("Đã thanh toán");

    private String description;

    public String getDescription() {
        return description;
    }

    OrderStatus(String description) {
        this.description = description;
    }
}