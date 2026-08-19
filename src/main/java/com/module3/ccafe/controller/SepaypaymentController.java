package com.module3.ccafe.controller;

import com.module3.ccafe.dto.request.SepayRequest;
import com.module3.ccafe.dto.response.SepayResponse;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.Payment;
import com.module3.ccafe.entity.PaymentGatewayLog;
import com.module3.ccafe.entity.enums.CallbackType;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.entity.enums.PaymentStatus;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.repository.PaymentGatewayLogRepository;
import com.module3.ccafe.repository.PaymentRepository;
import com.module3.ccafe.service.CafeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/sepay-payment")
@RequiredArgsConstructor
public class SepaypaymentController {

    final PaymentRepository paymentRepository;
    final OrderRepository orderRepository;
    final PaymentGatewayLogRepository paymentGatewayLogRepository;
    final ObjectMapper objectMapper;
    final CafeTableService cafeTableService;

    @Value("${sepay.api-key}")
    private String apiKey;

    @PostMapping
    @Transactional
    public ResponseEntity<SepayResponse> payment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SepayRequest request){
//        System.out.println(authHeader);
//        System.out.println(apiKey);
        try {
            if (!("Apikey " + apiKey).equals(authHeader)) {
                return ResponseEntity.status(401).body(
                        SepayResponse.builder().success(false).message("Unauthorized").build());
            }

            if (!"in".equals(request.getTransferType())) {
                return ResponseEntity.ok(
                        SepayResponse.builder().success(true).message("Bo qua giao dich tien ra").build());
            }

            String code = extractCode(request.getContent());
            Optional<Payment> paymentOpt = paymentRepository
                    .findByInternalTransactionCodeAndStatus(code, PaymentStatus.PENDING);

            if (paymentOpt.isEmpty()) {
                return ResponseEntity.ok(
                        SepayResponse.builder().success(true).message("Khong tim thay payment tuong ung").build());
            }

            Payment payment = paymentOpt.get();

            // Lưu log webhook gốc
            PaymentGatewayLog log = PaymentGatewayLog.builder()
                    .payment(payment)
                    .typeCallback(CallbackType.IPN)
                    .returnData(objectMapper.writeValueAsString(request))
                    .receivedAt(LocalDateTime.now())
                    .build();
            paymentGatewayLogRepository.save(log);

            if (payment.getTotal().longValue() != request.getTransferAmount()) {
                return ResponseEntity.ok(
                        SepayResponse.builder().success(true).message("Sai so tien, cho doi soat").build());
            }

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setGatewayTransactionCode(String.valueOf(request.getId()));
            payment.setConfirmedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            cafeTableService.closeTable(order.getOrderId());


            return ResponseEntity.ok(
                    SepayResponse.builder().success(true).message("Giao dich thanh cong").build());

        } catch (Exception exception){
            return ResponseEntity.ok(SepayResponse.builder()
                    .success(false)
                    .message("Giao dich that bai, xin vui long thu lai: " + exception.getMessage())
                    .build());
        }
    }

    private String extractCode(String content){
        Matcher m = Pattern.compile("DH\\d+").matcher(content);
        return m.find() ? m.group() : "";
    }
}