package com.module3.ccafe.entity.enums;

public enum ProductStatus {
    AVAILABLE("Đang bán"),
    OUT_OF_STOCK("Tạm hết hàng"),
    DISCONTINUED("Ngừng bán vĩnh viễn");

    private String description;

    public String getDescription() {
        return description;
    }

    ProductStatus(String description) {
        this.description = description;
    }
}
