package com.crown.care.dto;

/**
 * Admin login: username = email.
 * Employee login: username = email or employee ID (e.g. EMP001).
 */
public record LoginRequest(String username, String password) {
}
