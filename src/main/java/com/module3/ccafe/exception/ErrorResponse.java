package com.module3.ccafe.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    boolean success = false;
    int status;
    String message;
    String path;
    LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(int status, String message,String path){
        this.status = status;
        this.message = message;
        this.path = path;
    }

}
