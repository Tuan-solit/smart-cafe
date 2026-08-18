package com.module3.ccafe.repository;

import com.module3.ccafe.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od where od.order.orderId  = :orderId")
    BigDecimal sumTotalByOrderId(@Param("orderId") Integer orderId);

    List<OrderDetail> findByOrder_OrderId(Integer orderId);

    Optional<OrderDetail> findByOrder_OrderIdAndProduct_ProductId(Integer orderId, Integer productId);
  
    List<OrderDetail> findByOrderOrderId(Integer orderId);
  
    long countByOrderOrderId(Integer orderId);
   
}
