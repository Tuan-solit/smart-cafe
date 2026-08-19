package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.StaffRequest;
import com.module3.ccafe.entity.enums.UserStatus;
import com.module3.ccafe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffViewController {

    private final UserService userService;

    @GetMapping("/page")
    public String staffPage(Model model) {
        model.addAttribute("staffList", userService.getAllStaff());
        return "admin/staff/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("staffRequest", new StaffRequest());
        return "admin/staff/create";
    }

    @PostMapping("/create")
    public String createStaff(
            @ModelAttribute("staffRequest") StaffRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/staff/create";
        }

        try {
            userService.createStaff(request);

            model.addAttribute("success", true);
            return "admin/staff/create";

        } catch (RuntimeException e) {

            String message = e.getMessage();

            if ("Email đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "email",
                        "duplicate.email",
                        "Email này đã được sử dụng"
                );

            } else if ("Số điện thoại đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "phone",
                        "duplicate.phone",
                        "Số điện thoại này đã được sử dụng"
                );

            } else {

                model.addAttribute("error", message);
            }

            return "admin/staff/create";
        }
    }

    @GetMapping("/edit/{userId}")
    public String editPage(
            @PathVariable Integer userId,
            Model model) {

        var staff = userService.getStaffById(userId);

        StaffRequest request = new StaffRequest();
        request.setFullName(staff.getFullName());
        request.setPhone(staff.getPhone());
        request.setEmail(staff.getEmail());

        model.addAttribute("staffRequest", request);
        model.addAttribute("staff", staff);

        return "admin/staff/edit";
    }

    @PostMapping("/edit/{userId}")
    public String updateStaff(
            @PathVariable Integer userId,
            @ModelAttribute("staffRequest") StaffRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("staff", userService.getStaffById(userId));
            return "admin/staff/edit";
        }

        try {

            userService.updateStaff(userId, request);

            model.addAttribute("success", true);
            model.addAttribute("staff", userService.getStaffById(userId));
            model.addAttribute("staffRequest", request);

            return "admin/staff/edit";

        } catch (RuntimeException e) {

            String message = e.getMessage();

            if ("Email đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "email",
                        "duplicate.email",
                        "Email này đã được sử dụng"
                );

            } else if ("Số điện thoại đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "phone",
                        "duplicate.phone",
                        "Số điện thoại này đã được sử dụng"
                );

            } else {

                model.addAttribute("error", message);
            }

            model.addAttribute("staff", userService.getStaffById(userId));

            return "admin/staff/edit";
        }
    }

    @PostMapping("/{userId}/status")
    public String changeStatus(
            @PathVariable Integer userId,
            @RequestParam UserStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            userService.changeStatus(userId, status);

            String message = status == UserStatus.ACTIVE
                    ? "Đã kích hoạt tài khoản nhân viên!"
                    : "Đã vô hiệu hóa tài khoản nhân viên!";

            redirectAttributes.addFlashAttribute(
                    "success",
                    message
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/staff/page";
    }

    @PostMapping("/delete/{userId}")
    public String deleteStaff(
            @PathVariable Integer userId,
            RedirectAttributes redirectAttributes) {

        try {
            userService.deleteStaff(userId);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Xóa nhân viên thành công!"
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/staff/page";
    }
}