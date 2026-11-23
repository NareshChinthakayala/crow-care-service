package com.crown.care.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * One table for both admins and employees (stylists); the role column tells them apart.
 * employeeCode and specialty are only used for employees.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true)
    private String employeeCode;

    private String specialty;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    private LocalDateTime createdAt = LocalDateTime.now();
}
