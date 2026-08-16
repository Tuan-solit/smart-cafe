package com.module3.ccafe.service;

import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.Payment;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.entity.enums.PaymentMethod;
import com.module3.ccafe.entity.enums.PaymentStatus;
import com.module3.ccafe.repository.OrderDetailRepository;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.repository.PaymentRepository;
import com.module3.ccafe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentService {
    final OrderRepository orderRepository;
    final OrderDetailRepository orderDetailRepository;
    final PaymentRepository paymentRepository;
    final UserRepository userRepository;

    @Transactional
    public Payment confirmCashPayment(Integer orderId, Integer employeeId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Dơn hàng không tồn tại"));
        if(order.getStatus() == OrderStatus.PAID){
            throw new IllegalArgumentException("Đơn hàng đã được thanh toán");

        }
        BigDecimal total = orderDetailRepository.sumTotalByOrderId(orderId);

        if(total == null){
            throw new IllegalArgumentException("Đơn hàng chưa có món ăn, không thể thanh toán ");
        }
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(userRepository.getReferenceById(employeeId));
        payment.setTotal(total);
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setInternalTransactionCode(LocalDateTime.now().toString());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return payment;

    }
}
