package com.crown.care.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        LocalDate date,
        long totalOrdersToday,
        long pendingOrders,
        long inProgressOrders,
        long completedOrders,
        List<OrderResponse> todayOrders,
        List<EmployeeProgressResponse> employeeProgress,
        List<ProductResponse> lowStockProducts
) {
}
