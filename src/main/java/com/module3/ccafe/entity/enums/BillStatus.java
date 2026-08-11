package com.module3.ccafe.entity.enums;

public enum BillStatus {
    OPEN("Đang mở"),
    PAID("Đã thanh toán");

    private String description;

    public String getDescription() {
        return description;
    }

    BillStatus(String description) {
        this.description = description;
    }
}