package com.crown.care.dto;

import com.crown.care.entity.OrderStatus;
import com.crown.care.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerPhone,
        OrderType orderType,
        String serviceName,
        BigDecimal servicePrice,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String notes,
        OrderStatus status,
        BigDecimal totalAmount,
        Long employeeId,
        String employeeName,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
