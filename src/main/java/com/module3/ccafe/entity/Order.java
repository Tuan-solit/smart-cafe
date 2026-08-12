package com.module3.ccafe.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    Long id;

    @Column(name = "ThoiGianGoi")
    LocalDateTime orderTime;

    @ManyToOne
    @JoinColumn(name = "MaBan")
    CafeTable table;

    @ManyToOne
    @JoinColumn(name = "MaTK")
    User user;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    List<Payment> payments;
}
