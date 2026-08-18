package com.module3.ccafe.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
//        log.warn("404 - {}", ex.getMessage());
//        ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage(), request.getRequestURI());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(),request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f-> f.getField()+ ": "+ f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(400, message,request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
//        log.warn("404 - Route không tồn tại: {}", ex.getRequestURL());
//        ErrorResponse error = new ErrorResponse(404, "Không tìm thấy tài nguyên", ex.getRequestURL());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneral(Exception exception,  HttpServletRequest request){
//        log.error("Lỗi không xác định",exception);
//        ErrorResponse errorResponse = new ErrorResponse(500, "Lỗi hệ thống, vui lòng thử lại sau", request.getRequestURI());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//
//    }
}
