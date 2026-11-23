package com.crown.care.dto;

import com.crown.care.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * employeeId is optional: an order can be assigned now or later.
 * items are optional products sold or used with the service.
 */
public record OrderRequest(
        String customerName,
        String customerPhone,
        OrderType orderType,
        String serviceName,
        BigDecimal servicePrice,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String notes,
        Long employeeId,
        List<OrderItemRequest> items
) {
}
