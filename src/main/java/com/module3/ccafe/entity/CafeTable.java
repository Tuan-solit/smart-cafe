package com.module3.ccafe.entity;


import com.module3.ccafe.entity.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "table_number", nullable = false, unique = true,length = 10)
    private String tableNumber;

    @Lob
    @Column(name = "url_qr", columnDefinition = "LONGTEXT")
    private String urlQr;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TableStatus status;

    @OneToMany(mappedBy = "table")
    private List<Order> orders;
}
