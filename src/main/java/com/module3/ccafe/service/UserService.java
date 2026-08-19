package com.module3.ccafe.service;

import com.module3.ccafe.dto.StaffRequest;
import com.module3.ccafe.dto.StaffResponse;
import com.module3.ccafe.dto.request.UpdateProfileRequest;
import com.module3.ccafe.dto.response.UpdateProfileResponse;
import com.module3.ccafe.entity.Role;
import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.repository.RoleRepository;
import com.module3.ccafe.repository.UserRepository;
import com.module3.ccafe.security.CustomUserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String EMPLOYEE_ROLE = "EMPLOYEE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        return userRepository.findUserByRoleName(EMPLOYEE_ROLE).stream().map(this::toStaffResponse).toList();
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Integer userId) {
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE).
                orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        return toStaffResponse(user);
    }

    public StaffResponse createStaff(StaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        Role staffRole = roleRepository.findByName(EMPLOYEE_ROLE).orElseThrow(() -> new RuntimeException("Không tìm thấy Role STAFF"));

        User user = User.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(staffRole)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return toStaffResponse(savedUser);
    }

    public StaffResponse updateStaff(Integer userId, StaffRequest request) {
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        if (!Objects.equals(user.getEmail(), request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (!Objects.equals(user.getPhone(), request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return toStaffResponse(updatedUser);
    }

    public void deleteStaff(Integer userId) {
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        userRepository.delete(user);
    }

    public StaffResponse changeStatus(Integer userId, UserStatus status) {
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        user.setStatus(status);
        return toStaffResponse(userRepository.save(user));
    }

    private StaffResponse toStaffResponse(User user) {
        return StaffResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getName())
                .status(user.getStatus())
                .build();
    }


    @Transactional
    public UpdateProfileResponse updateProfile(Integer userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        if (!user.getPhone().equals(req.getPhone())
                && userRepository.findByPhone(req.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())
                && userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        userRepository.save(user);

        CustomUserPrincipal updatedPrincipal = new CustomUserPrincipal(user);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedPrincipal,
                SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                updatedPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);


        return UpdateProfileResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

    }
    public long countEmployee () {
        return userRepository.countUsersByRoleName("EMPLOYEE");

    }
}
