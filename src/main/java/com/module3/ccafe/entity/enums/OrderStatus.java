package com.module3.ccafe.entity.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    IN_PROGRESS("Đang thực hiện"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    private String description;

    public String getDescription() {
        return description;
    }

    OrderStatus(String description) {
        this.description = description;
    }
}
