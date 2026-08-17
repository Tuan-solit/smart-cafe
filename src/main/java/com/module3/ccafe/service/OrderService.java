package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.CreateOrderByEmployeeRequest;
import com.module3.ccafe.dto.response.OrderDetailViewResponse;
import com.module3.ccafe.dto.response.OrderItemResponse;
import com.module3.ccafe.entity.*;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.entity.enums.TableStatus;
import com.module3.ccafe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.OrderDetail;
import com.module3.ccafe.repository.OrderDetailRepository;
import com.module3.ccafe.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderService {
    private static final String ORDER_ID_SESSION_KEY = "ORDER_ID";

    final CafeTableRepository cafeTableRepository;
    final OrderRepository orderRepository;
    final UserRepository userRepository;
    final ActivityLogRepository activityLogRepository;
    final OrderDetailRepository orderDetailRepository;
    final ProductRepository productRepository;

    @Transactional
    public Order openTableByEmployee(Integer tableId, Integer employeeUserId){
        CafeTable table = cafeTableRepository.findById(tableId).orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));
        if(table.getStatus() == TableStatus.IN_SERVICE){
            return orderRepository.findByTable_TableIdAndStatus(tableId, OrderStatus.OPEN)
                    .orElseThrow(() -> new IllegalArgumentException("Bàn đang IN_SERVICE nhưng không có order open"));
        }
        Order order = new Order();
        order.setTable(table);
        order.setUser(userRepository.getReferenceById(employeeUserId));
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);
        orderRepository.save(order);

        table.setStatus(TableStatus.IN_SERVICE);
        cafeTableRepository.save(table);

        ActivityLog log = new ActivityLog();
        log.setUser(userRepository.getReferenceById(employeeUserId));
        log.setAction("Mở bàn hộ khách - table_id= "+ tableId + ", order_id=" +order.getOrderId());

        activityLogRepository.save(log);
        return order;
    }

    public Page<Order> getPendingOrders(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return orderRepository.findByStatusOrderByCreatedAt(OrderStatus.OPEN,pageable);
    }



    public OrderDetailViewResponse getOrderDetail(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        List<OrderDetail> items = orderDetailRepository.findByOrder_OrderId(orderId);

        List<OrderItemResponse> itemResponses = items.stream()
                .map(d -> OrderItemResponse.builder()
                        .productId(d.getProduct().getProductId())
                        .productName(d.getProduct().getName())
                        .quantity(d.getQuantity())
                        .price(d.getPrice())
                        .note(d.getNote())
                        .build())
                .toList();

        BigDecimal total = items.stream()
                .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderDetailViewResponse.builder()
                .orderId(order.getOrderId())
                .tableNumber(order.getTable().getTableNumber())
                .status(order.getStatus().toString())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .total(total)
                .build();
    }

    @Transactional
    public void addItemToOrder(Integer orderId, Integer productId, Integer quantity, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng đã thanh toán, không thể thêm món");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new IllegalArgumentException("Sản phẩm hiện không khả dụng");
        }

        Optional<OrderDetail> existing = orderDetailRepository
                .findByOrder_OrderIdAndProduct_ProductId(orderId, productId);

        if (existing.isPresent()) {
            // đã có món này trong đơn -> cộng dồn số lượng (theo unique constraint bạn đã thêm)
            OrderDetail detail = existing.get();
            detail.setQuantity(detail.getQuantity() + quantity);
            if (note != null) detail.setNote(note);
            orderDetailRepository.save(detail);
        } else {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(product.getPrice()); // chốt giá tại thời điểm order
            detail.setNote(note);
            orderDetailRepository.save(detail);
        }
    }

    @Transactional
    public void updateItemQuantity(Integer orderId, Integer productId, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItemFromOrder(orderId, productId);
            return;
        }
        OrderDetail detail = orderDetailRepository
                .findByOrder_OrderIdAndProduct_ProductId(orderId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Món này không có trong đơn"));

        if (detail.getOrder().getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng đã thanh toán, không thể sửa");
        }
        detail.setQuantity(newQuantity);
        orderDetailRepository.save(detail);
    }

    @Transactional
    public void removeItemFromOrder(Integer orderId, Integer productId) {
        OrderDetail detail = orderDetailRepository
                .findByOrder_OrderIdAndProduct_ProductId(orderId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Món này không có trong đơn"));

        if (detail.getOrder().getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng đã thanh toán, không thể xóa món");
        }
        orderDetailRepository.delete(detail);
    }

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
