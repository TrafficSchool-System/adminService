package com.example.adminService.Security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtUtil {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, Long adminId);

    boolean isTokenValid(String token, UserDetails userDetails);

    @Deprecated
    Long extractAdminId(String token);

    Long extractUserId(String token);

    String extractEmail(String token);

    String extractRole(String token);
}
