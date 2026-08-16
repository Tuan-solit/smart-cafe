package com.module3.ccafe.controller;

import com.module3.ccafe.dto.request.ChangePasswordRequest;
import com.module3.ccafe.dto.request.RegisterRequest;
import com.module3.ccafe.dto.response.ChangePasswordResponse;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }


    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @PostMapping("/auth/register")
    public String register(RegisterRequest registerRequest, RedirectAttributes redirectAttributes) {
        try {
            authService.register(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công, vui lòng đăng nhập");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/auth/change-password")
    public String changePassword(ChangePasswordRequest password,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 RedirectAttributes redirectAttributes){
        ChangePasswordResponse res = authService.changePassword(principal.getUserId(), password);
        redirectAttributes.addFlashAttribute("message", res.getMessage());
        return "redirect:/employee/dashboard";
    }

}
