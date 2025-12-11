package com.example.adminService.Service;

import com.example.adminService.Dto.AdminLoginRequest;
import com.example.adminService.Dto.AdminLoginResponse;
import com.example.adminService.Dto.AdminResponseDTO;
import com.example.adminService.Entity.Admin;
import com.example.adminService.Repository.AdminRepository;
import com.example.adminService.Security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService implements AdminServiceInterface {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
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

        // STEG 4: Uppdatera lastLogin
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

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

    @Override
    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return AdminResponseDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .active(admin.isActive())
                .build();
    }

    @Override
    public String validateTokenAndGetRole(String token) {
        return jwtService.extractRole(token);
    }
}