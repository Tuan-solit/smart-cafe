package com.module3.ccafe.repository;

import com.module3.ccafe.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderId(Integer orderId);
    long countByOrderOrderId(Integer orderId);
    
}
