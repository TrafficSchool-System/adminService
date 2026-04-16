package com.example.adminService.Service;

import com.example.adminService.Dto.CompleteUserDetailsDto;
import com.example.adminService.Dto.CreateUserWithSubscriptionDTO;
import com.example.adminService.Dto.UpdateUserRequestDto;
import com.example.adminService.Dto.UserBasicInfoDto;
import com.example.adminService.Dto.UserWithSubscriptionResponseDTO;
import java.util.List;

public interface UserManagementServiceInterface {

    /**
     * Hämta komplett information om en specifik användare
     * Inkluderar användarinfo, prenumerationer och betalningar
     */
    CompleteUserDetailsDto getCompleteUserDetails(Long userId);

    /**
     * Hämta komplett information om alla användare
     * Används för admin-översikt
     */
    List<CompleteUserDetailsDto> getAllUsersWithDetails();

    /**
     * Skapa användare med prenumeration (admin operation)
     * Orchestrator som skapar användare OCH tilldelar paket i en operation
     */
    UserWithSubscriptionResponseDTO createUserWithSubscription(CreateUserWithSubscriptionDTO request);

    /**
     * Uppdatera användarinformation
     */
    UserBasicInfoDto updateUserInfo(Long userId, UpdateUserRequestDto updateRequest);
}
