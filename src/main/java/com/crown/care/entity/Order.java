package com.crown.care.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Offline order (walk-in / phone). For the assigned stylist this is an "appointment".
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    private String customerPhone;

    @Enumerated(EnumType.STRING)
    private OrderType orderType = OrderType.WALK_IN;

    private String serviceName;

    private BigDecimal servicePrice = BigDecimal.ZERO;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User assignedEmployee;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;
}
