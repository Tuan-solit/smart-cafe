package com.module3.ccafe.controller.customer;

import com.module3.ccafe.dto.request.CashPaymentRequest;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.Payment;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.service.OrderService;
import com.module3.ccafe.service.PaymentService;
import com.module3.ccafe.service.WebSocketService;
import jakarta.jws.WebParam;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;
    private final PaymentService paymentService;
    
    @PostMapping("/cash/request")
    public String requestCashPayment(@RequestParam Integer orderId, RedirectAttributes redirectAttributes){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException("Đơn hàng không tồn tại"));
        CashPaymentRequest request = CashPaymentRequest.builder()
                .orderId(order.getOrderId())
                .tableNumber(order.getTable().getTableNumber())
                .message("Bàn "+order.getTable().getTableNumber()+" yêu cầu thanh toán tiền mặt")
                .createdAt(LocalDateTime.now()).build();
        webSocketService.sendCashPaymentRequest(request);
        redirectAttributes.addFlashAttribute("successMessage","Yêu cầu thành công! Vui lòng đến quầy thu ngân để thanh toán.");
        return "redirect:/menu";
    }
    
    @GetMapping("/{orderId}/online")
    public String showOnlinePaymentQr(@PathVariable Integer orderId, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        Integer sessionOrderId = (Integer) session.getAttribute("ORDER_ID_SESSION_KEY");
        if (sessionOrderId == null) {
            sessionOrderId = (Integer) session.getAttribute("ORDER_ID");
        }
        Integer tableId = (Integer) session.getAttribute("TABLE_ID_SESSION_KEY");
        if (tableId == null) {
            tableId = (Integer) session.getAttribute("TABLE_ID");
        }
        if(sessionOrderId == null || !sessionOrderId.equals(orderId)){
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên gọi món không hơpj lệ, vui lòng quét lại mã qr tại bàn");
            return tableId == null? "redirect:/menu?tableId" + tableId: "redirect:/menu";
        }
        try{
        Payment payment = paymentService.createOnlinePayment(orderId);
        String qrUrl = paymentService.buildQrUrl(payment);
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("paymentId", payment.getPaymentId());
        model.addAttribute("orderId",orderId);
        model.addAttribute("total",payment.getTotal());
        return "customer/payment-qr";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/menu?tableId=" + tableId;
        }
    }
    
    @GetMapping("/payments/{paymentId}/status")
    @ResponseBody
    public Map<String, String> checkPaymentStatus(@PathVariable Integer paymentId){
        Payment payment = paymentService.findById(paymentId);
        return Map.of("status",payment.getStatus().name());
    }
}
