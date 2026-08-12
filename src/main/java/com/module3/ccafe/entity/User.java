package com.module3.ccafe.entity;


import com.module3.ccafe.entity.enums.UserStatus;
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
public class User {
    @Id
    Long id;
    String fullName;
    String phone;
    String password;

    @Enumerated(EnumType.STRING)
    UserStatus userStatus;

    @ManyToOne
    @JoinColumn(name = "id")
    Role role;

    @OneToMany(mappedBy = "user")
    List<ActivityLog> activityLog;
}
