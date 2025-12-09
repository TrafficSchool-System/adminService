package com.example.adminService.Controller;

import com.example.adminService.Dto.AdminLoginRequest;
import com.example.adminService.Dto.AdminLoginResponse;
import com.example.adminService.Service.AdminServiceInterface;
import com.example.adminService.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminServiceInterface adminService;
    private final JwtService jwtService;

    // PUBLIC ENDPOINT - Ingen autentisering krävs
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    // PROTECTED ENDPOINT - Kräver ADMIN role
    @GetMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Ta bort "Bearer "

        String username = jwtService.extractUsername(token);
        Long adminId = jwtService.extractAdminId(token);
        String role = jwtService.extractRole(token);

        return ResponseEntity.ok(Map.of(
                "adminId", adminId,
                "username", username,
                "role", role));
    }

    // PUBLIC ENDPOINT - Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Admin Auth Service is running");
    }
}