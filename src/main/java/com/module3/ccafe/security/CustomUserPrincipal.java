package com.module3.ccafe.security;

import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserPrincipal implements UserDetails {
    final Integer userId;
    final  String fullName;
    final String phone;
    final String email;
    final String password;
    final boolean enabled;
    final Collection<? extends GrantedAuthority> authorities;


    public CustomUserPrincipal(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        this.enabled = user.getStatus() == UserStatus.ACTIVE;
    }


    @Override
    public String getUsername() {
        return phone;
    }

}
