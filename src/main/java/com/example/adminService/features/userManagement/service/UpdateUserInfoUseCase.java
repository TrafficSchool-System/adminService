package com.example.adminService.features.userManagement.service;

import com.example.adminService.features.userManagement.dto.UpdateUserRequestDto;
import com.example.adminService.features.userManagement.dto.UserBasicInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ==========================================
 * USE CASE: UPDATE USER INFO
 * ==========================================
 * Ansvar: Uppdatera användarinformation via UserService
 * 
 * BUSINESS LOGIC:
 * 1. Anropa UserService PUT /api/admin/users/{id}
 * 2. Returnera uppdaterad user-info
 * 
 * SERVICE-TO-SERVICE KOMMUNIKATION:
 * AdminService → UserService (via WebClient + internal API key)
 * 
 * ANVÄNDS AV: UserManagementController.updateUser()
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserInfoUseCase {

    private final WebClient userServiceWebClient;

    @Value("${service.api.key}")
    private String serviceApiKey;

    /**
     * Uppdatera user information
     * 
     * @param userId        User ID
     * @param updateRequest Uppdaterad user-info
     * @return Uppdaterad UserBasicInfoDto
     * @throws RuntimeException om uppdatering misslyckas
     */
    public UserBasicInfoDto execute(Long userId, UpdateUserRequestDto updateRequest) {
        log.info("📝 Updating user info: userId={}", userId);

        try {
            UserBasicInfoDto updatedUser = userServiceWebClient
                    .put()
                    .uri("/api/admin/users/{id}", userId)
                    .header("X-Internal-API-Key", serviceApiKey)
                    .bodyValue(updateRequest)
                    .retrieve()
                    .bodyToMono(UserBasicInfoDto.class)
                    .block();

            log.info("✅ User info updated successfully: userId={}", updatedUser.getId());

            return updatedUser;
        } catch (Exception e) {
            log.error("❌ Failed to update user info: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("Could not update user: " + e.getMessage(), e);
        }
    }
}
