package com.module3.ccafe.service;

import com.module3.ccafe.dto.response.PaymentHistoryResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    String bankAccount;

    @Value("${sepay.bank-name}")
    String bankName;

    @Value("${sepay.accountHolder}")
    String accountHolder;


    @Transactional
    public Payment confirmCashPayment(
            Integer orderId,
            Integer employeeId
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Đơn hàng không tồn tại"
                        )
                );

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException(
                    "Đơn hàng đã được thanh toán"
            );
        }

        BigDecimal total =
                orderDetailRepository.sumTotalByOrderId(orderId);

        if (total == null) {
            throw new IllegalArgumentException(
                    "Đơn hàng chưa có món ăn, không thể thanh toán"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setUser(
                userRepository.getReferenceById(employeeId)
        );

        payment.setTotal(total);
        payment.setPaymentMethod(
                PaymentMethod.CASH
        );

        payment.setInternalTransactionCode(
                generatePaymentCode(orderId)
        );

        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        payment.setCreatedAt(now);
        payment.setConfirmedAt(now);
        paymentRepository.save(payment);
        order.setStatus(
                OrderStatus.PAID
        );

        orderRepository.save(order);
        return payment;
    }


    @Transactional
    public Payment createOnlinePayment(
            Integer orderId,
            Integer employeeId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Đơn hàng không tồn tại"
                        )
                );

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException(
                    "Đơn hàng đã được thanh toán"
            );
        }


        Optional<Payment> existing =
                paymentRepository
                        .findByOrder_OrderIdAndStatus(
                                orderId,
                                PaymentStatus.PENDING
                        );

        if (existing.isPresent()) {
            return existing.get();
        }


        BigDecimal total =
                orderDetailRepository.sumTotalByOrderId(orderId);

        if (total == null) {
            throw new IllegalArgumentException(
                    "Đơn hàng chưa có món ăn, không thể thanh toán"
            );
        }


        String code =
                generatePaymentCode(orderId);

        LocalDateTime now =
                LocalDateTime.now();


        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(
                userRepository.getReferenceById(employeeId)
        );

        payment.setTotal(total);
        payment.setPaymentMethod(
                PaymentMethod.ONLINE
        );

        payment.setStatus(
                PaymentStatus.PENDING
        );

        payment.setInternalTransactionCode(
                code
        );

        payment.setCreatedAt(
                now
        );

        paymentRepository.save(payment);
        return payment;
    }


    public String buildQrUrl(
            Payment payment
    ) {

        return "https://qr.sepay.vn/img?"
                + "acc="
                + URLEncoder.encode(
                bankAccount,
                StandardCharsets.UTF_8
        )

                + "&bank="
                + URLEncoder.encode(
                bankName,
                StandardCharsets.UTF_8
        )

                + "&amount="
                + payment.getTotal().longValue()
                + "&des="
                + URLEncoder.encode(
                payment.getInternalTransactionCode(),
                StandardCharsets.UTF_8
        )

                + "&template=compact"
                + "&showinfo=false"
                + "&holder="
                + URLEncoder.encode(
                accountHolder,
                StandardCharsets.UTF_8
        );
    }


    public Payment findById(
            Integer paymentId
    ) {

        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment không tồn tại"
                        )
                );
    }


    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponse> getPaymentHistory(
            int page,
            int size,
            String keyword,
            PaymentStatus status,
            PaymentMethod paymentMethod
    ) {

        Pageable pageable =
                PageRequest.of(page, size);


        return paymentRepository.searchPayments(
                        keyword,
                        status,
                        paymentMethod,
                        pageable
                )
                .map(payment ->
                        PaymentHistoryResponse.builder()

                                .paymentId(
                                        payment.getPaymentId()
                                )

                                .orderId(
                                        payment.getOrder()
                                                .getOrderId()
                                )

                                .tableNumber(
                                        payment.getOrder()
                                                .getTable()
                                                .getTableNumber()
                                )

                                .employeeName(
                                        payment.getUser() != null
                                                ? payment.getUser()
                                                  .getFullName()
                                                : "N/A"
                                )

                                .total(
                                        payment.getTotal()
                                )

                                .paymentMethod(
                                        payment.getPaymentMethod()
                                                .name()
                                )

                                .internalTransactionCode(
                                        payment
                                                .getInternalTransactionCode()
                                )

                                .gatewayTransactionCode(
                                        payment
                                                .getGatewayTransactionCode()
                                )

                                .status(
                                        payment.getStatus()
                                                .name()
                                )

                                .statusDescription(
                                        payment.getStatus()
                                                .getDescription()
                                )

                                .createdAt(
                                        payment.getCreatedAt()
                                )

                                .confirmedAt(
                                        payment.getConfirmedAt()
                                )

                                .build()
                );
    }


    private String generatePaymentCode(
            Integer orderId
    ) {

        long timestampPart =
                System.currentTimeMillis() % 100000;

        String suffix =
                String.format(
                        "%03d%05d",
                        orderId % 1000,
                        timestampPart
                );

        return "DH" + suffix;
    }

}