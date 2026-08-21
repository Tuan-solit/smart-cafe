package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.response.ProductStatisticsResponse;
import com.module3.ccafe.service.ProductStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/product-statistics")
@RequiredArgsConstructor
public class ProductStatisticsController {

    private final ProductStatisticsService productStatisticsService;


    @GetMapping
    public String productStatistics(

            @RequestParam(
                    value = "period",
                    defaultValue = "7"
            )
            int period,

            Model model
    ) {

        LocalDate endDate =
                LocalDate.now();

        LocalDate startDate;


        switch (period) {

            case 1:
                // Hôm nay
                startDate = endDate;
                break;

            case 30:
                // 30 ngày
                startDate = endDate.minusDays(29);
                break;

            case 7:
            default:
                // 7 ngày
                startDate = endDate.minusDays(6);
                period = 7;
                break;
        }


        List<ProductStatisticsResponse> statistics =
                productStatisticsService.getStatistics(
                        startDate,
                        endDate
                );


        Long totalQuantity =
                productStatisticsService.getTotalQuantity(
                        statistics
                );


        BigDecimal totalRevenue =
                productStatisticsService.getTotalRevenue(
                        statistics
                );


        model.addAttribute(
                "statistics",
                statistics
        );

        model.addAttribute(
                "totalQuantity",
                totalQuantity
        );

        model.addAttribute(
                "totalRevenue",
                totalRevenue
        );

        model.addAttribute(
                "period",
                period
        );

        model.addAttribute(
                "startDate",
                startDate
        );

        model.addAttribute(
                "endDate",
                endDate
        );


        return "admin/product-statistics";
    }
}