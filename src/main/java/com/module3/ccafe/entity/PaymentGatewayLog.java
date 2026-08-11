package com.module3.ccafe.entity;

import com.module3.ccafe.entity.enums.CallbackType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentGatewayLog {

    @Id
    Long id;

    @Enumerated(EnumType.STRING)
    CallbackType callbackType;

    String receivedData;

    LocalDateTime receivedAt;

    @ManyToOne
    @JoinColumn(name = "MaTT")
    Payment payment;
}