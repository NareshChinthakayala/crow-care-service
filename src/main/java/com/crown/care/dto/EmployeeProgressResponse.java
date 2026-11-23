package com.crown.care.dto;

public record EmployeeProgressResponse(
        Long employeeId,
        String employeeCode,
        String fullName,
        long totalAssigned,
        long pending,
        long inProgress,
        long done
) {
}
