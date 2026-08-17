package com.module3.ccafe.service;

import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.OrderDetail;
import com.module3.ccafe.repository.OrderDetailRepository;
import com.module3.ccafe.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String ORDER_ID_SESSION_KEY = "ORDER_ID";

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Order getCurrentOrder(HttpSession session) {
        Integer orderId = (Integer) session.getAttribute(ORDER_ID_SESSION_KEY);
        if (orderId == null) {
            return null;
        }
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<OrderDetail> getCurrentOrderDetails(HttpSession session) {
        Integer orderId = (Integer) session.getAttribute(ORDER_ID_SESSION_KEY);
        if (orderId == null) {
            return List.of();
        }
        List<OrderDetail> details = orderDetailRepository.findByOrderOrderId(orderId);
        return details.stream().collect(Collectors.groupingBy(detail -> detail.getProduct().getProductId(),
                Collectors.reducing((d1,d2)->{
                    OrderDetail merged = new OrderDetail();
                    merged.setProduct(d1.getProduct());
                    merged.setPrice(d1.getPrice());
                    merged.setQuantity(d1.getQuantity()+d2.getQuantity());
                    return merged;
                })
        )).values().stream().flatMap(Optional::stream).toList();
    }

    public long getCurrentOrderDetailCount(HttpSession session) {
        Integer orderId = (Integer) session.getAttribute(ORDER_ID_SESSION_KEY);
        if (orderId == null) {
            return 0;
        }
        return orderDetailRepository.countByOrderOrderId(orderId);
    }

    public BigDecimal getCurrentOrderTotal(HttpSession session) {
        List<OrderDetail> details = getCurrentOrderDetails(session);
        return details.stream().map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
