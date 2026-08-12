package com.module3.ccafe.entity;

import com.module3.ccafe.entity.enums.CallbackType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_gateway_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_log_id")
    private Integer pLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_callback", nullable = false, length = 10)
    private CallbackType typeCallback;

    @Lob
    @Column(name = "return_data", columnDefinition = "TEXT")
    private String returnData;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;
}