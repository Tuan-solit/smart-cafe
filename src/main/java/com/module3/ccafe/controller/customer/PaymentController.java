package com.module3.ccafe.controller.customer;

import com.module3.ccafe.dto.request.CashPaymentRequest;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.service.OrderService;
import com.module3.ccafe.service.PaymentService;
import com.module3.ccafe.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;
    
    @PostMapping("/cash/request")
    public String requestCashPayment(@RequestParam Integer orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException("Đơn hàng không tồn tại"));
        CashPaymentRequest request = CashPaymentRequest.builder()
                .orderId(order.getOrderId())
                .tableNumber(order.getTable().getTableNumber())
                .message("Bàn "+order.getTable().getTableNumber()+" yêu cầu thanh toán tiền mặt")
                .createdAt(LocalDateTime.now()).build();
        webSocketService.sendCashPaymentRequest(request);
        return "redirect:/menu";
    }
}
