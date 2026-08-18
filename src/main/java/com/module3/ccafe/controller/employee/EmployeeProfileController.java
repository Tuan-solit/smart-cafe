package com.module3.ccafe.controller.employee;

import com.module3.ccafe.dto.request.ChangePasswordRequest;
import com.module3.ccafe.dto.request.UpdateProfileRequest;
import com.module3.ccafe.dto.response.ChangePasswordResponse;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.AuthService;
import com.module3.ccafe.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/profile")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal CustomUserPrincipal principal, Model model){
        model.addAttribute("updateProfileRequest", UpdateProfileRequest.builder()
                .fullName(principal.getFullName())
                .phone(principal.getPhone())
                .email(principal.getEmail())
                .build());
        return "employee/profile";
    }

    @PostMapping
    public String updateProfile(@Valid UpdateProfileRequest req,
                                @AuthenticationPrincipal CustomUserPrincipal principal,
                                RedirectAttributes redirectAttributes){
        try {
            userService.updateProfile(principal.getUserId(), req);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(ChangePasswordRequest password,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 RedirectAttributes redirectAttributes){
        ChangePasswordResponse res = authService.changePassword(principal.getUserId(), password);
        redirectAttributes.addFlashAttribute("message", res.getMessage());
        return "redirect:/employee/profile";
    }
}