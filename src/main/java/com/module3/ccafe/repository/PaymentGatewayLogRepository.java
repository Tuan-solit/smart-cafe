package com.module3.ccafe.repository;

import com.module3.ccafe.entity.PaymentGatewayLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayLogRepository extends JpaRepository<PaymentGatewayLog,Integer> {
}
