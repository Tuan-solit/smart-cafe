package com.module3.ccafe.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL("USER_001","Email đã được sử dụng"),
    DUPLICATE_PHONE("USER_002","Phone đã được sử dụng");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
