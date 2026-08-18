package com.module3.ccafe.controller.employee;

import com.module3.ccafe.dto.response.ActivityLogResponse;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employee/activity-logs")
@RequiredArgsConstructor
public class EmployeeActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public String myLogs(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @AuthenticationPrincipal CustomUserPrincipal principal,
                         Model model){
        Page<ActivityLogResponse> logs = activityLogService.getMyActivityLogs(principal.getUserId(), page, size);
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        return "employee/activity-logs";
    }
}