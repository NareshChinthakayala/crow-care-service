package com.crown.care.security;

import com.crown.care.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Creates and reads short-lived JWT access tokens.
 * (Refresh tokens are random strings stored in the refresh_tokens table.)
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpiryMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-token-expiry-ms}") long accessTokenExpiryMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = accessTokenExpiryMs;
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .claim("salonId", user.getSalon().getId())
                .claim("name", user.getFullName())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiryMs))
                .signWith(key)
                .compact();
    }

    /** Returns the user id from a valid token; throws if the token is invalid or expired. */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public long getAccessTokenExpiryMs() {
        return accessTokenExpiryMs;
    }
}
