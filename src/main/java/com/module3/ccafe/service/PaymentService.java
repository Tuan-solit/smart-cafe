package com.module3.ccafe.service;

import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.Payment;
import com.module3.ccafe.entity.User;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentService {
    final OrderRepository orderRepository;
    final OrderDetailRepository orderDetailRepository;
    final PaymentRepository paymentRepository;
    final UserRepository userRepository;

    @Value("${sepay.bank-account}")
    private String bankAccount;
    @Value("${sepay.bank-name}")
    private String bankName;
    @Value("${sepay.accountHolder}")
    private String accountHolder;
    
    //nhan vien tao payment giup khach
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
        payment.setInternalTransactionCode(generateTransactionCode(orderId));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return payment;

    }

    private String generateTransactionCode(Integer orderId) {
        return "CASH-"
                + orderId
                + "-"
                + System.currentTimeMillis();
    }

    public String buildQrUrl(Payment payment) {
        return "https://qr.sepay.vn/img?"
                + "acc=" + URLEncoder.encode(bankAccount, StandardCharsets.UTF_8)
                + "&bank=" + URLEncoder.encode(bankName, StandardCharsets.UTF_8)
                + "&amount=" + payment.getTotal().longValue()
                + "&des=" + URLEncoder.encode(payment.getInternalTransactionCode(), StandardCharsets.UTF_8)
                + "&template=compact"
                + "&showinfo=false"
                + "&holder=" + URLEncoder.encode(accountHolder, StandardCharsets.UTF_8); // thêm config mới
    }
    public Payment findByid(Integer paymentId){
        return paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));
    }


    @Transactional
    public Payment createOnlinePayment(Integer orderId, Integer employeeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng đã được thanh toán");
        }

        List<Payment> oldPendings = paymentRepository
                .findAllByOrder_OrderIdAndStatus(orderId, PaymentStatus.PENDING);
        for (Payment old : oldPendings) {
            old.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(old);
        }

        BigDecimal total = orderDetailRepository.sumTotalByOrderId(orderId);
        if (total == null) {
            throw new IllegalArgumentException("Đơn hàng chưa có món ăn, không thể thanh toán");
        }

        String code = generatePaymentCode(orderId);

        Payment payment = Payment.builder()
                .order(order)
                .user(userRepository.getReferenceById(employeeId))
                .total(total)
                .paymentMethod(PaymentMethod.ONLINE)
                .status(PaymentStatus.PENDING)
                .internalTransactionCode(code)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return payment;
    }


    private String generatePaymentCode(Integer orderId) {
        long timestampPart = System.currentTimeMillis() % 100000;
        String suffix = String.format("%03d%05d", orderId % 1000, timestampPart);
        return "DH" + suffix;
    }

    @Transactional
    public void cancelPayment(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }


    public Payment findById(Integer paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }
    }
