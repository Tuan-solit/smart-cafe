package com.module3.ccafe.dto.request;

import com.module3.ccafe.dto.response.RecentOrderResponse;
import com.module3.ccafe.entity.Order;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDashboarRequest {
    int todayOrderCount;
    int pendingOrderCount;
    int completedOrderCount;
    BigDecimal shiftRevenue;
    List<RecentOrderResponse> recentOrders;
}
