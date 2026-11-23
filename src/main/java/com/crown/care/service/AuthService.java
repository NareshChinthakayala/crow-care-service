package com.crown.care.service;

import com.crown.care.dto.*;
import com.crown.care.entity.RefreshToken;
import com.crown.care.entity.Role;
import com.crown.care.entity.Salon;
import com.crown.care.entity.User;
import com.crown.care.exception.ApiException;
import com.crown.care.repository.RefreshTokenRepository;
import com.crown.care.repository.SalonRepository;
import com.crown.care.repository.UserRepository;
import com.crown.care.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw ApiException.badRequest("Email is already registered");
        }
        if (req.confirmPassword() != null && !req.confirmPassword().equals(req.password())) {
            throw ApiException.badRequest("Passwords do not match");
        }

        Salon salon = new Salon();
        salon.setName(req.salonName());
        salon.setAddress(req.salonAddress());
        salon.setPhone(req.phone());
        salon.setEmail(req.email());
        salonRepository.save(salon);

        User admin = new User();
        admin.setFullName(req.fullName());
        admin.setEmail(req.email());
        admin.setPhone(req.phone());
        admin.setPassword(passwordEncoder.encode(req.password()));
        admin.setRole(Role.ADMIN);
        admin.setSalon(salon);
        userRepository.save(admin);

        return buildAuthResponse(admin);
    }

    @Transactional
    public AuthResponse adminLogin(LoginRequest req) {
        User user = userRepository.findByEmail(req.username())
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        checkPassword(req.password(), user);
        return buildAuthResponse(user);
    }

    /** Employees can log in with their email or their employee ID (e.g. EMP001). */
    @Transactional
    public AuthResponse employeeLogin(LoginRequest req) {
        User user = userRepository.findByEmailOrEmployeeCode(req.username(), req.username())
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .orElseThrow(() -> ApiException.unauthorized("Invalid email/employee ID or password"));
        if (!user.isActive()) {
            throw ApiException.unauthorized("Your account is inactive. Contact your admin.");
        }
        checkPassword(req.password(), user);
        return buildAuthResponse(user);
    }

    /** Swap a valid refresh token for a new access token + new refresh token. */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest req) {
        RefreshToken stored = refreshTokenRepository.findByToken(req.refreshToken())
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        refreshTokenRepository.delete(stored);
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            throw ApiException.unauthorized("Refresh token expired, please log in again");
        }
        return buildAuthResponse(stored.getUser());
    }

    @Transactional
    public void logout(RefreshTokenRequest req) {
        refreshTokenRepository.findByToken(req.refreshToken()).ifPresent(refreshTokenRepository::delete);
    }

    private void checkPassword(String raw, User user) {
        if (raw == null || !passwordEncoder.matches(raw, user.getPassword())) {
            throw ApiException.unauthorized("Invalid credentials");
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiryMs));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                refreshToken.getToken(),
                "Bearer",
                jwtService.getAccessTokenExpiryMs(),
                Mapper.toUser(user));
    }
}
