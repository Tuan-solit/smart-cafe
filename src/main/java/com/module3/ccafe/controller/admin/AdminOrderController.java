package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.response.OrderDetailViewResponse;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;


    /**
     * Danh sách tất cả đơn hàng
     */
    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {

        Page<Order> orderPage =
                orderService.getAllOrders(page, size);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);

        return "admin/order/list";
    }


    /**
     * Chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public String orderDetail(
            @PathVariable Integer orderId,
            Model model
    ) {

        try {

            OrderDetailViewResponse orderDetail =
                    orderService.getOrderDetail(orderId);

            model.addAttribute(
                    "orderDetail",
                    orderDetail
            );

            return "admin/order/detail";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "admin/order/detail";
        }
    }
}