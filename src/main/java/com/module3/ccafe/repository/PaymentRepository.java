package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    @Query("""
    SELECT SUM(p.total) FROM Payment p
    WHERE p.user.userId = :userId
    AND p.status = 'SUCCESS'
    AND p.confirmedAt BETWEEN :start AND :end
""")
    BigDecimal sumRevenueByUserAndDateRange(@Param("userId") Integer userId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}
