package com.module3.ccafe.entity;

import com.module3.ccafe.entity.enums.PaymentStatus;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    Long id;

    BigDecimal amount;


    String paymentMethod;

    String internalTransactionCode;

    String gatewayTransactionCode;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    @Column(name = "ThoiGianTao")
    LocalDateTime createdAt;

    @Column(name = "ThoiGianXacNhan")
    LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "MaDH")
    Order order;

    @ManyToOne
    @JoinColumn(name = "MaTK")
    User user;

    @OneToMany(mappedBy = "payment")
    List<PaymentGatewayLog> gatewayLogs;

}
