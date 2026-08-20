package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Payment;
import com.module3.ccafe.entity.enums.PaymentMethod;
import com.module3.ccafe.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("""
        SELECT SUM(p.total)
        FROM Payment p
        WHERE p.user.userId = :userId
        AND p.status = 'SUCCESS'
        AND p.confirmedAt BETWEEN :start AND :end
    """)
    BigDecimal sumRevenueByUserAndDateRange(
            @Param("userId") Integer userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Optional<Payment> findByOrder_OrderIdAndStatus(
            Integer orderId,
            PaymentStatus status
    );

    Optional<Payment> findByInternalTransactionCodeAndStatus(
            String code,
            PaymentStatus status
    );

    List<Payment> findAllByOrder_OrderIdAndStatus(Integer orderId, PaymentStatus status);


    @Query("""
        SELECT p
        FROM Payment p
        WHERE (:keyword IS NULL
               OR :keyword = ''
               OR LOWER(p.internalTransactionCode)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.gatewayTransactionCode)
                    LIKE LOWER(CONCAT('%', :keyword, '%')))

        AND (:status IS NULL OR p.status = :status)

        AND (:paymentMethod IS NULL
             OR p.paymentMethod = :paymentMethod)
        ORDER BY p.createdAt DESC
    """)
    Page<Payment> searchPayments(
            @Param("keyword") String keyword,
            @Param("status") PaymentStatus status,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(SUM(p.total), 0)
    FROM Payment p
    WHERE p.status = :status
    AND p.confirmedAt >= :start
    AND p.confirmedAt < :end
""")
    BigDecimal sumRevenueByStatusAndConfirmedAtBetween(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}