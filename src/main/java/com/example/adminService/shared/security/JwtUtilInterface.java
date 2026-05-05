package com.example.adminService.shared.security;

import com.example.adminService.shared.security.AdminDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtilInterface implements JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // milliseconds

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(UserDetails userDetails, Long adminId) {
        // Hämta Admin entity från AdminDetailsImpl för att få email
        AdminDetailsImpl adminDetails = (AdminDetailsImpl) userDetails;
        String email = adminDetails.getAdmin().getEmail();

        Map<String, Object> extraClaims = new HashMap<>();
        // Använd SAMMA claim-namn som UserService + Gateway förväntar
        extraClaims.put("userId", adminId); // Gateway letar efter "userId", inte "adminId"
        extraClaims.put("email", email); // Lägg till email claim
        extraClaims.put("role", "ADMIN");

        // Subject ska vara email (inte username) för kompatibilitet med Gateway
        return generateTokenWithEmail(extraClaims, email);
    }

    // Ny helper-metod för att sätta email som subject
    private String generateTokenWithEmail(Map<String, Object> extraClaims, String email) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email) // Email som subject (inte username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Subject är nu email (inte username) för nya tokens
        final String subjectEmail = extractUsername(token); // extractUsername läser subject (som är email nu)

        // För AdminDetailsImpl, hämta email från admin-objektet
        if (userDetails instanceof AdminDetailsImpl) {
            AdminDetailsImpl adminDetails = (AdminDetailsImpl) userDetails;
            String adminEmail = adminDetails.getAdmin().getEmail();
            return (subjectEmail.equals(adminEmail) && !isTokenExpired(token));
        }

        // Fallback för andra UserDetails implementationer (backward compatibility)
        return (subjectEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    @Deprecated
    public Long extractAdminId(String token) {
        // Deprecated: Gamla tokens använder "adminId", nya använder "userId"
        return extractClaim(token, claims -> claims.get("adminId", Long.class));
    }

    @Override
    public Long extractUserId(String token) {
        // Standardiserat claim-namn som matchar UserService och Gateway
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public String extractEmail(String token) {
        // Email kan vara både i subject och som egen claim
        return extractClaim(token, claims -> {
            String email = claims.get("email", String.class);
            return email != null ? email : claims.getSubject();
        });
    }

    @Override
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // ========== PRIVATE METODER ==========

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}