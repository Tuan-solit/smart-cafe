package com.module3.ccafe.controller;

import com.module3.ccafe.dto.request.EmployeeDashboarRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    @GetMapping("dashboard")
    public String dashboard(Model model){
        EmployeeDashboarRequest employeeDashboarRequest =EmployeeDashboarRequest.builder()
                .todayOrderCount(0)
                .completedOrderCount(0)
                .pendingOrderCount(0)
                .shiftRevenue(0)
                .recentOrders(List.of())
                .build();
        model.addAttribute("dashboard",employeeDashboarRequest);
        return "employee/dashboard";
    }
}
