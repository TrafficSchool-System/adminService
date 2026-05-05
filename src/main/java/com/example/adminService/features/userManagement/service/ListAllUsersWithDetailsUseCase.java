package com.example.adminService.features.userManagement.service;

import com.example.adminService.features.userManagement.dto.CompleteUserDetailsDto;
import com.example.adminService.features.userManagement.dto.UserBasicInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ==========================================
 * USE CASE: LIST ALL USERS WITH DETAILS
 * ==========================================
 * Ansvar: Hämta lista med alla användare med komplett aggregerad info
 * 
 * BUSINESS LOGIC:
 * 1. Hämta alla users från UserService
 * 2. För varje user: Hämta kompletta detaljer via GetCompleteUserDetailsUseCase
 * 3. Returnera lista med aggregerad data
 * 
 * SERVICE-TO-SERVICE KOMMUNIKATION:
 * AdminService → UserService (för att få user-lista)
 * Sedan återanvänder GetCompleteUserDetailsUseCase för aggregering
 * 
 * PERFORMANCE NOTE:
 * Detta kan bli långsamt för många users eftersom vi gör N+1 anrop.
 * För produktion: överväg batch-endpoints eller caching.
 * 
 * ANVÄNDS AV: UserManagementController.getAllUsers()
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListAllUsersWithDetailsUseCase {

    private final WebClient userServiceWebClient;
    private final GetCompleteUserDetailsUseCase getCompleteUserDetailsUseCase;

    @Value("${service.api.key}")
    private String serviceApiKey;

    /**
     * Hämta alla users med kompletta detaljer (subscriptions + payments +
     * statistik)
     * 
     * @return Lista med CompleteUserDetailsDto för alla användare
     */
    public List<CompleteUserDetailsDto> execute() {
        log.debug("Fetching all users with details...");

        // STEG 1: Hämta alla users från UserService
        List<UserBasicInfoDto> allUsers = fetchAllUsersFromUserService();

        if (allUsers.isEmpty()) {
            log.warn("⚠️ No users found in system");
            return new ArrayList<>();
        }

        log.debug("Fetched {} users, aggregating details...", allUsers.size());

        // STEG 2: För varje user, hämta kompletta detaljer
        List<CompleteUserDetailsDto> usersWithDetails = allUsers.stream()
                .map(user -> getCompleteUserDetailsUseCase.execute(user.getId()))
                .collect(Collectors.toList());

        log.info("All users fetched: totalUsers={}", usersWithDetails.size());

        return usersWithDetails;
    }

    /**
     * Hämta alla users från UserService
     */
    private List<UserBasicInfoDto> fetchAllUsersFromUserService() {
        try {
            log.debug("🔄 Calling UserService to get all users...");

            List<UserBasicInfoDto> users = userServiceWebClient
                    .get()
                    .uri("/api/admin/users")
                    .header("X-Internal-API-Key", serviceApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UserBasicInfoDto>>() {
                    })
                    .block();

            int userCount = users != null ? users.size() : 0;
            log.debug("Successfully fetched {} users from UserService", userCount);

            return users != null ? users : new ArrayList<>();
        } catch (Exception e) {
            log.error("❌ Error fetching users from UserService: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
