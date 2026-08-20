package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.response.PaymentHistoryResponse;
import com.module3.ccafe.entity.enums.PaymentMethod;
import com.module3.ccafe.entity.enums.PaymentStatus;
import com.module3.ccafe.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;


    @GetMapping
    public String getPaymentHistory(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(required = false)
            PaymentMethod paymentMethod,

            Model model
    ) {

        Page<PaymentHistoryResponse> paymentPage =
                paymentService.getPaymentHistory(
                        page,
                        size,
                        keyword,
                        status,
                        paymentMethod
                );


        model.addAttribute(
                "paymentPage",
                paymentPage
        );

        model.addAttribute(
                "paymentList",
                paymentPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "pageSize",
                size
        );


        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "status",
                status
        );

        model.addAttribute(
                "paymentMethod",
                paymentMethod
        );


        model.addAttribute(
                "paymentStatuses",
                PaymentStatus.values()
        );

        model.addAttribute(
                "paymentMethods",
                PaymentMethod.values()
        );


        return "admin/payment/list";
    }

}