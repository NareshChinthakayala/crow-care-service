package com.crown.care.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String category,
        String description,
        BigDecimal price,
        int stockQuantity,
        int lowStockThreshold,
        boolean lowStock,
        String imageUrl,
        LocalDateTime createdAt
) {
}
