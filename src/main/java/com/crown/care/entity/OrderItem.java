package com.crown.care.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A product sold or used as part of an order. The name and price are copied so the
 * order history stays correct even if the product changes or is deleted later.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String productName;

    private int quantity;

    private BigDecimal price = BigDecimal.ZERO;
}
