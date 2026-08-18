package com.module3.ccafe.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {
//    private static  final Logger log = LoggerFactory.getLogger(CustomErrorController.class);
//
//    @RequestMapping("/error")
//    public  Object handleError(HttpServletRequest request, Model model){
//        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        Object pathObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
//        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//
//        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;
//        String path = pathObj != null ?pathObj.toString() : "unknown" ;
//
//        log.warn("Lỗi {} tại đường dẫn: {} - message: {}",statusCode,path, messageObj);
//
//        model.addAttribute("path",path);
//        model.addAttribute("statusCode", statusCode);
//
//        return switch (statusCode){
//            case 404 -> "error/404";
//            case 403 -> "error/403";
//            case 500 -> "error/500";
//            default -> "error/default";
//        };
//    }

    private String getMessageForStatus(int statusCode){
        return switch (statusCode){
            case 404 -> "Không tìm thấy tài nguyên";
            case 403 -> "Không có quyền truy cập";
            case 500 -> "Lỗi hệ thống, vui lòng thử lại sau";
            default -> "Đã có lỗi xảy ra";
        };
    }
}
