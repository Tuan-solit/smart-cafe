package com.module3.ccafe.security;

import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user =  userRepository.findByPhone(phone).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với số điện thoại: " + phone));

        return new CustomUserPrincipal(user);
    }
}
