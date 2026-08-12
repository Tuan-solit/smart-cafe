package com.module3.ccafe.entity.enums;

public enum TableStatus {
    AVAILABLE("Trống"),
    IN_SERVICE("Đang phục vụ");

    private String description;

    public String getDescription() {
        return description;
    }

    TableStatus(String description) {
        this.description = description;
    }
}
