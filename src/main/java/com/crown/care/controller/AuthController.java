package com.crown.care.controller;

import com.crown.care.dto.*;
import com.crown.care.entity.User;
import com.crown.care.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Admin registration screen (creates the salon + admin account). */
    @PostMapping("/admin/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerAdmin(@RequestBody AdminRegisterRequest request) {
        return authService.registerAdmin(request);
    }

    /** Admin login screen: { "username": "admin@crowncare.com", "password": "admin123" } */
    @PostMapping("/admin/login")
    public AuthResponse adminLogin(@RequestBody LoginRequest request) {
        return authService.adminLogin(request);
    }

    /** Stylist login screen: username can be email or employee ID (EMP001). */
    @PostMapping("/employee/login")
    public AuthResponse employeeLogin(@RequestBody LoginRequest request) {
        return authService.employeeLogin(request);
    }

    /** Get a new access token using the refresh token. */
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public MessageResponse logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return new MessageResponse("Logged out successfully");
    }

    /** Current logged-in user (admin or employee). */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return Mapper.toUser(user);
    }
}
