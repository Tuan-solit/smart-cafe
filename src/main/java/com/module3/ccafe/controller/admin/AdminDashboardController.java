package com.module3.ccafe.controller.admin;

import com.module3.ccafe.repository.CafeTableRepository;
import com.module3.ccafe.service.OrderService;
import com.module3.ccafe.service.PaymentService;
import com.module3.ccafe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final OrderService orderService;
    private final CafeTableRepository cafeTableRepository;
    private final PaymentService paymentService;


    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Số lượng nhân viên
        model.addAttribute(
                "employeeCount",
                userService.countEmployee()
        );

        // Số lượng bàn
        model.addAttribute(
                "tableCount",
                cafeTableRepository.count()
        );

        model.addAttribute(
                "todayOrderCount",
                orderService.countTodayOrders()
        );

        model.addAttribute(
                "todayRevenue",
                paymentService.getTodayRevenue()
        );

        return "admin/dashboard";
    }
}