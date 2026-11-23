package com.crown.care.dto;

import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String fullName,
        String email,
        String phone,
        String specialty,
        boolean active,
        LocalDateTime createdAt
) {
}
