package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository <Order,Integer>{
    Optional<Order>  findByTable_TableIdAndStatus(Integer tableId, OrderStatus status);

    Page<Order> findByStatusOrderByCreatedAt(OrderStatus status, Pageable pageable);

    List<Order> findByTable_TableIdInAndStatus(List<Integer> tableIds, OrderStatus status);


    long countByUser_UserIdAndCreatedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);

    long countByUser_UserIdAndStatusAndCreatedAtBetween(Integer userId, OrderStatus status, LocalDateTime start, LocalDateTime end);

    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
