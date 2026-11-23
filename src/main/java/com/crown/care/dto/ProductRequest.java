package com.crown.care.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String category,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Integer lowStockThreshold,
        String imageUrl
) {
}
