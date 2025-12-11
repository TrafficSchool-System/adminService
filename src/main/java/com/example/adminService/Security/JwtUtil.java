package com.example.adminService.Security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtUtil {
    
    // Extrahera username från token
    String extractUsername(String token);
    
    // Extrahera en specifik claim
    <T> T extractClaim(String token, Function<Claims, T> claimResolver);
    
    // Generera token från UserDetails
    String generateToken(UserDetails userDetails);
    
    // Generera token med extra claims (adminId, role)
    String generateToken(UserDetails userDetails, Long adminId);
    
    // Validera token
    boolean isTokenValid(String token, UserDetails userDetails);
    
    // Extrahera adminId från token
    Long extractAdminId(String token);
    
    // Extrahera role från token
    String extractRole(String token);
}