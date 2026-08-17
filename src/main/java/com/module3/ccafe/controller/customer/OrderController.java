package com.module3.ccafe.controller.customer;

import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.OrderDetail;
import com.module3.ccafe.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/current")
    public String currentOrder(HttpSession session, Model model) {
        Order order = orderService.getCurrentOrder(session);
        if (order == null) {
            return "redirect:/menu";
        }
        List<OrderDetail> orderDetails = orderService.getCurrentOrderDetails(session);
        BigDecimal total = orderService.getCurrentOrderTotal(session);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("total", total);

        return "customer/order";
    }
}
