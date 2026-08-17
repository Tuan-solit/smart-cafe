package com.module3.ccafe.controller.employee;

import com.module3.ccafe.dto.request.EmployeeDashboarRequest;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.EmployeeDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeDashboardService employeeDashboardService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserPrincipal principal, Model model){
        EmployeeDashboarRequest dashboard = employeeDashboardService.getDashboard(principal.getUserId());
        model.addAttribute("dashboard", dashboard);
        return "employee/dashboard";
    }
}
