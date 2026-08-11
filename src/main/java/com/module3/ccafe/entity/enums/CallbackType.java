package com.module3.ccafe.entity.enums;

public enum CallbackType {
    RETURN("Return URL"),
    IPN("IPN - Server to Server");

    private String description;

    public String getDescription() {
        return description;
    }

    CallbackType(String description) {
        this.description = description;
    }
}
