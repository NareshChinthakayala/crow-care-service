package com.crown.care.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs,
        UserResponse user
) {
}
