package com.module3.ccafe.service;

import com.module3.ccafe.dto.response.ProductStatisticsResponse;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatisticsService {

    private final OrderDetailRepository orderDetailRepository;


    /**
     * Thống kê sản phẩm theo khoảng thời gian
     */
    public List<ProductStatisticsResponse> getStatistics(
            LocalDate startDate,
            LocalDate endDate
    ) {

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1).atStartOfDay();

        return orderDetailRepository.getProductStatistics(
                OrderStatus.PAID,
                start,
                end
        );
    }


    /**
     * Tổng số lượng sản phẩm đã bán
     */
    public Long getTotalQuantity(
            List<ProductStatisticsResponse> statistics
    ) {

        return statistics.stream()
                .mapToLong(ProductStatisticsResponse::getTotalQuantity)
                .sum();
    }


    /**
     * Tổng doanh thu
     */
    public BigDecimal getTotalRevenue(
            List<ProductStatisticsResponse> statistics
    ) {

        return statistics.stream()
                .map(ProductStatisticsResponse::getTotalRevenue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}