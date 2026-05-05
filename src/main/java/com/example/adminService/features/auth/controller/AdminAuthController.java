package com.example.adminService.features.auth.controller;

import com.example.adminService.features.auth.dto.AdminLoginRequest;
import com.example.adminService.features.auth.dto.AdminLoginResponse;
import com.example.adminService.features.auth.service.LoginAdminUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ADMIN AUTH CONTROLLER
 * 
 * RESTful endpoints for admin authentication.
 * Base path: /api/admin/auth
 * 
 * AUTHENTICATION ENDPOINTS:
 * - POST /admin/auth/login : Admin login with email/password
 * - GET /admin/auth/tokens : Validate current admin token
 * \n * AUTHENTICATION:
 * - POST /login is public (no authentication)
 * - GET /tokens requires ADMIN role (validated by Gateway)
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final LoginAdminUseCase loginAdminUseCase;

    /**
     * ADMIN LOGIN
     * POST /api/admin/auth/login
     * 
     * Public endpoint for admin authentication.
     * Returns JWT token for admin access.
     * 
     * @param request Login credentials (email, password)
     * @return Admin login response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = loginAdminUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * VALIDATE ADMIN TOKEN
     * GET /api/admin/auth/tokens
     * 
     * Protected endpoint to validate current admin token.
     * Gateway has already validated JWT and set headers.
     * Returns admin user information from headers.
     * 
     * @param userId User ID from Gateway header
     * @param email  Email from Gateway header
     * @param role   Role from Gateway header
     * @return Admin user information
     */
    @GetMapping("/tokens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        // Gateway has already validated JWT and set headers
        // We just read these headers to return user info
        return ResponseEntity.ok(Map.of(
                "adminId", userId,
                "email", email,
                "role", role));
    }

    // PUBLIC ENDPOINT - Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Admin Auth Service is running");
    }
}