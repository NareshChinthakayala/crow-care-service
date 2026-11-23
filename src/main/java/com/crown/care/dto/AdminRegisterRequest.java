package com.crown.care.dto;

public record AdminRegisterRequest(
        String salonName,
        String fullName,
        String email,
        String phone,
        String salonAddress,
        String password,
        String confirmPassword
) {
}
