package com.example.adminService.features.auth.service;

import com.example.adminService.features.auth.dto.AdminResponseDTO;
import com.example.adminService.features.auth.entity.Admin;
import com.example.adminService.features.auth.mapper.AdminMapper;
import com.example.adminService.features.auth.repository.AdminRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ==========================================
 * USE CASE: GET ADMIN
 * ==========================================
 * Ansvar: Hämta admin-information by ID
 * 
 * BUSINESS LOGIC:
 * 1. Hämta admin från databas
 * 2. Konvertera till DTO (via Mapper)
 * 3. Returnera admin-info
 * 
 * OBS: Vi har endast 1 admin-konto i systemet
 * 
 * ANVÄNDS AV: AdminAuthController (om behövs för profil-visning)
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetAdminUseCase {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    /**
     * Hämta admin by ID
     * 
     * @param adminId Admin ID
     * @return AdminResponseDTO med admin-info
     * @throws RuntimeException om admin inte hittas
     */
    public AdminResponseDTO execute(Long adminId) {
        log.debug("📋 Fetching admin: id={}", adminId);

        // STEG 1: Hämta admin från databas
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));

        // STEG 2: Konvertera till DTO
        AdminResponseDTO response = adminMapper.toResponseDTO(admin);

        log.debug("✅ Admin fetched successfully: username={}", admin.getUsername());

        return response;
    }
}
