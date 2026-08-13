package com.module3.ccafe.service;

import com.module3.ccafe.dto.StaffRequest;
import com.module3.ccafe.dto.StaffResponse;
import com.module3.ccafe.entity.Role;
import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.repository.RoleRepository;
import com.module3.ccafe.repository.UserRepository;
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
                orElseThrow(()-> new RuntimeException("Không tìm thấy nhân viên"));

        return toStaffResponse(user);
    }

    public StaffResponse createStaff(StaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        Role staffRole = roleRepository.findByName(EMPLOYEE_ROLE).orElseThrow(()-> new RuntimeException("Không tìm thấy Role STAFF"));

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
                .orElseThrow(()-> new RuntimeException("Không tìm thấy nhân viên"));

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
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE).orElseThrow(()-> new RuntimeException("Không tìm thấy nhân viên"));
        userRepository.delete(user);
    }

    public StaffResponse changeStatus(Integer userId, UserStatus status) {
        User user = userRepository.findUserByIdAndRoleName(userId, EMPLOYEE_ROLE).orElseThrow(()-> new RuntimeException("Không tìm thấy nhân viên"));
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
}
