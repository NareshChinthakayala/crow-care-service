package com.crown.care.dto;

/**
 * Used for create and update. On update, a null/blank password keeps the old one.
 */
public record EmployeeRequest(
        String fullName,
        String email,
        String phone,
        String specialty,
        String password,
        Boolean active
) {
}
