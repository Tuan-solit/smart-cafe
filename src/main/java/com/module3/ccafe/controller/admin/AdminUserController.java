package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.StaffRequest;
import com.module3.ccafe.dto.StaffResponse;
import com.module3.ccafe.dto.StaffUpdateRequest;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<StaffResponse> getAllStaff() {
        return userService.getAllStaff();
    }

    @GetMapping("/{userId}")
    public StaffResponse getStaffById(@PathVariable Integer userId) {
        return userService.getStaffById(userId);
    }

    @PostMapping
    public StaffResponse createStaff(@RequestBody StaffRequest request) {
        return userService.createStaff(request);
    }

    @PutMapping("/{userId}")
    public StaffResponse updateStaff(@PathVariable Integer userId, @RequestBody StaffRequest request) {
        return userService.updateStaff(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteStaff(@PathVariable Integer userId) {
        userService.deleteStaff(userId);
    }

    @PatchMapping("/{userId}/status")
    public StaffResponse changeStatus(@PathVariable Integer userId, @RequestParam UserStatus status) {
        return userService.changeStatus(userId, status);
    }
}
