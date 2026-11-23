package com.crown.care.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(length = 1000)
    private String description;

    private BigDecimal price = BigDecimal.ZERO;

    private int stockQuantity;

    private int lowStockThreshold = 5;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isLowStock() {
        return stockQuantity <= lowStockThreshold;
    }
}
