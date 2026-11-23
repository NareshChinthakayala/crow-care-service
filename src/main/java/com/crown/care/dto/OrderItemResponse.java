package com.crown.care.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal price
) {
}
