package com.module3.ccafe.entity;


import com.module3.ccafe.entity.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CafeTable {
    @Id
    Long id;
    String tableNumber;
    String qrCode;

    @Enumerated(EnumType.STRING)
    TableStatus tableStatus;

    @OneToMany(mappedBy = "table")
    List<Order> order;
}
