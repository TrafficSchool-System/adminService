package com.example.adminService.features.auth.mapper;

import com.example.adminService.features.auth.dto.AdminResponseDTO;
import com.example.adminService.features.auth.entity.Admin;
import org.springframework.stereotype.Component;

/**
 * ==========================================
 * ADMIN MAPPER
 * ==========================================
 * Konverterar mellan Admin-entity och DTOs
 * 
 * CLEAN ARCHITECTURE PATTERN:
 * Entity → DTO (outbound till frontend)
 * 
 * @author Senior Java Developer
 */
@Component
public class AdminMapper {

    /**
     * Konvertera Admin entity till AdminResponseDTO
     * 
     * @param admin Admin entity
     * @return AdminResponseDTO för frontend
     */
    public AdminResponseDTO toResponseDTO(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminResponseDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .active(admin.isActive())
                .build();
    }
}
