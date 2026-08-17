package com.module3.ccafe.controller.employee;

import com.module3.ccafe.dto.request.SearchCafeTableRequest;
import com.module3.ccafe.dto.response.SearchCafeTableResponse;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.CafeTableService;
import com.module3.ccafe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/tables")
@RequiredArgsConstructor
public class EmployeeTableController {

    private final CafeTableService cafeTableService;
    private final OrderService orderService;

    @GetMapping
    public String list(@ModelAttribute SearchCafeTableRequest searchCafeTableRequest,
                       @PageableDefault(page = 0, size = 10) Pageable pageable,
                       Model model){
        Page<SearchCafeTableResponse> result = cafeTableService.search(searchCafeTableRequest, pageable);
        model.addAttribute("cafeTables", result);
        model.addAttribute("searchCafeTableRequest", searchCafeTableRequest);
        return "employee/table-list";
    }

    @PostMapping("/{tableId}/open")
    public String openTable(@PathVariable Integer tableId,
                            @AuthenticationPrincipal CustomUserPrincipal principal,
                            RedirectAttributes redirectAttributes){
        try {
            Order order = orderService.openTableByEmployee(tableId, principal.getUserId());
            redirectAttributes.addFlashAttribute("message", "Mở bàn thành công");
            return "redirect:/employee/orders/" + order.getOrderId();
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee/tables";
        }
    }

    @PostMapping("/{orderId}/close")
    public String closeTable(@PathVariable Integer orderId, RedirectAttributes redirectAttributes){
        try {
            cafeTableService.closeTable(orderId);
            redirectAttributes.addFlashAttribute("message", "Đóng bàn thành công");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/tables";
    }
}