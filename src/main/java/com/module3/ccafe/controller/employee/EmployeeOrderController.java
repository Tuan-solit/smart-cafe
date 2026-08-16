package com.module3.ccafe.controller.employee;

import com.module3.ccafe.dto.response.OrderDetailViewResponse;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.repository.ProductRepository;
import com.module3.ccafe.security.CustomUserPrincipal;
import com.module3.ccafe.service.OrderService;
import com.module3.ccafe.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/orders")
@RequiredArgsConstructor
public class EmployeeOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductRepository productRepository;

    @GetMapping
    public String listPendingOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model){
        Page<Order> orderPage = orderService.getPendingOrders(page, size);
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        return "employee/order-list";
    }

    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, Model model){
        try {
            OrderDetailViewResponse detail = orderService.getOrderDetail(orderId);
            model.addAttribute("orderDetail", detail);
            model.addAttribute("products", productRepository.findByStatus(ProductStatus.AVAILABLE));
            return "employee/order-detail";
        } catch (IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "employee/order-detail";
        }
    }

    @PostMapping("/{orderId}/items")
    public String addItem(@PathVariable Integer orderId,
                          @RequestParam Integer productId,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) String note,
                          RedirectAttributes redirectAttributes){
        try {
            orderService.addItemToOrder(orderId, productId, quantity, note);
            redirectAttributes.addFlashAttribute("message", "Thêm món thành công");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/orders/" + orderId;
    }

    @PostMapping("/{orderId}/items/{productId}/update")
    public String updateItem(@PathVariable Integer orderId,
                             @PathVariable Integer productId,
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes){
        try {
            orderService.updateItemQuantity(orderId, productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thành công");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/orders/" + orderId;
    }

    @PostMapping("/{orderId}/items/{productId}/remove")
    public String removeItem(@PathVariable Integer orderId,
                             @PathVariable Integer productId,
                             RedirectAttributes redirectAttributes){
        try {
            orderService.removeItemFromOrder(orderId, productId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa món");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/orders/" + orderId;
    }

    @PostMapping("/{orderId}/confirm-cash")
    public String confirmCashPayment(@PathVariable Integer orderId,
                                     @AuthenticationPrincipal CustomUserPrincipal principal,
                                     RedirectAttributes redirectAttributes){
        try {
            paymentService.confirmCashPayment(orderId, principal.getUserId());
            redirectAttributes.addFlashAttribute("message", "Xác nhận thanh toán thành công");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/orders/" + orderId;
    }
}