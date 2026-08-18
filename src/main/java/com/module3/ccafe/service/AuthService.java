package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.ChangePasswordRequest;
import com.module3.ccafe.dto.request.LoginRequest;
import com.module3.ccafe.dto.request.RegisterRequest;
import com.module3.ccafe.dto.response.ChangePasswordResponse;
import com.module3.ccafe.dto.response.LoginResponse;
import com.module3.ccafe.dto.response.RegisterResponse;
import com.module3.ccafe.entity.Role;
import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.repository.RoleRepository;
import com.module3.ccafe.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthService {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final AuthenticationManager authenticationManager;

    final RoleRepository roleRepository;

    public LoginResponse login(LoginRequest  loginRequest, HttpServletRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhone(),
                        loginRequest.getPassword()
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication((authentication));
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,context);


        User user = userRepository.findByPhone(loginRequest.getPhone()).orElseThrow(() ->new UsernameNotFoundException("Không tìm thấy người dùng theo tài khoản"));

        return LoginResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().getName())
                .build();
    }

    public RegisterResponse register(RegisterRequest registerRequest){
        if(userRepository.findByPhone(registerRequest.getPhone()).isPresent()){
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");
        }
        Role role = roleRepository.findById(2).orElseThrow();

        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .phone(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }


    public ChangePasswordResponse changePassword(Integer idUser, ChangePasswordRequest req){
        User user = userRepository.findById(idUser).orElseThrow();

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            return ChangePasswordResponse.builder().message("Sai mật khẩu, vui lòng nhập lại").build();
        }
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ChangePasswordResponse.builder().message("Mật khẩu không trùng khớp").build();
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return ChangePasswordResponse.builder().message("Mật khẩu thay đổi thành công").build();
    }

}
