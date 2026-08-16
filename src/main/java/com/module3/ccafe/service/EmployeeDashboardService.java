package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.EmployeeDashboarRequest;
import com.module3.ccafe.dto.response.RecentOrderResponse;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDashboardService {
    final OrderRepository orderRepository;
    final PaymentRepository paymentRepository;

    public EmployeeDashboarRequest getDashboard(Integer userId){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long todayOrderCount = orderRepository.countByUser_UserIdAndCreatedAtBetween(userId, startOfDay, now);
        long pendingOrderCount = orderRepository.countByUser_UserIdAndStatusAndCreatedAtBetween(userId, OrderStatus.OPEN, startOfDay, now);
        long completedOrderCount = orderRepository.countByUser_UserIdAndStatusAndCreatedAtBetween(userId, OrderStatus.PAID, startOfDay, now);

        BigDecimal revenue = paymentRepository.sumRevenueByUserAndDateRange(userId, startOfDay, now);
        if (revenue == null) revenue = BigDecimal.ZERO;

        List<RecentOrderResponse> recentOrders = orderRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5))
                .stream()
                .map(o -> RecentOrderResponse.builder()
                        .orderId(o.getOrderId())
                        .tableNumber(o.getTable().getTableNumber())
                        .status(o.getStatus().toString())
                        .total(o.getOrderDetails() != null
                                ? o.getOrderDetails().stream()
                                .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                : BigDecimal.ZERO)
                        .build())
                .toList();

        return EmployeeDashboarRequest.builder()
                .todayOrderCount((int) todayOrderCount)
                .pendingOrderCount((int) pendingOrderCount)
                .completedOrderCount((int) completedOrderCount)
                .shiftRevenue(revenue)
                .recentOrders(recentOrders)
                .build();
    }
}