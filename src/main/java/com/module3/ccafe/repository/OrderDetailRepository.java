package com.module3.ccafe.repository;

import com.module3.ccafe.entity.OrderDetail;
import com.module3.ccafe.dto.response.ProductStatisticsResponse;
import com.module3.ccafe.entity.enums.OrderStatus;
import java.time.LocalDateTime;
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

    @Query("""
        SELECT new com.module3.ccafe.dto.response.ProductStatisticsResponse(
            od.product.productId,
            od.product.name,
            SUM(od.quantity),
            SUM(od.price * od.quantity)
        )
        FROM OrderDetail od
        WHERE od.order.status = :status
          AND od.order.createdAt >= :start
          AND od.order.createdAt < :end
        GROUP BY od.product.productId, od.product.name
        ORDER BY SUM(od.quantity) DESC
        """)
    List<ProductStatisticsResponse> getProductStatistics(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
   
}
