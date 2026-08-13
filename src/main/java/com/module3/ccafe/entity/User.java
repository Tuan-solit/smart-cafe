package com.module3.ccafe.entity;


import com.module3.ccafe.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", length = 40)
    private String phone;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UserStatus status;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    private List<ActivityLog> activityLogs;
}