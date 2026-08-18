package com.module3.ccafe.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode errorCode;
    private final HttpStatus status;

    public BusinessException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }

    public BusinessException(ErrorCode errorCode, HttpStatus  status,String customMessage ){
        super(customMessage);
        this.errorCode = errorCode;
        this.status = status;
    }
}
