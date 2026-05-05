package com.example.adminService.features.auth.service;

import com.example.adminService.features.auth.dto.AdminLoginRequest;
import com.example.adminService.features.auth.dto.AdminLoginResponse;
import com.example.adminService.features.auth.entity.Admin;
import com.example.adminService.features.auth.repository.AdminRepository;
import com.example.adminService.shared.security.AdminDetailsImpl;
import com.example.adminService.shared.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ==========================================
 * USE CASE: LOGIN ADMIN
 * ==========================================
 * Ansvar: Autentisera admin och generera JWT token
 * 
 * BUSINESS LOGIC:
 * 1. Validera credentials (Spring Security + BCrypt)
 * 2. Generera JWT token med adminId och role
 * 3. Uppdatera lastLogin timestamp
 * 4. Returnera token + admin info
 * 
 * ANVÄNDS AV: AdminAuthController.login()
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAdminUseCase {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Utför admin-login och returnerar JWT token
     * 
     * @param request Login request med username och password
     * @return AdminLoginResponse med token och admin-info
     * @throws org.springframework.security.core.AuthenticationException om
     *                                                                   credentials
     *                                                                   är
     *                                                                   felaktiga
     */
    public AdminLoginResponse execute(AdminLoginRequest request) {
        log.info("🔐 Admin login attempt: username={}", request.getUsername());

        // STEG 1: Autentisera med Spring Security (kollar BCrypt password automatiskt)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // STEG 2: Hämta AdminDetailsImpl från authentication
        AdminDetailsImpl adminDetails = (AdminDetailsImpl) authentication.getPrincipal();
        Admin admin = adminDetails.getAdmin();

        // STEG 3: Generera JWT token med adminId och role
        String token = jwtService.generateToken(adminDetails, admin.getId());

        // STEG 4: Uppdatera lastLogin timestamp
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        log.info("✅ Admin login successful: adminId={}, username={}", admin.getId(), admin.getUsername());

        // STEG 5: Returnera response
        return AdminLoginResponse.builder()
                .token(token)
                .adminId(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .build();
    }
}
