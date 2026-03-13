package com.example.adminService.Service;

import com.example.adminService.Dto.CompleteUserDetailsDto;
import com.example.adminService.Dto.UpdateUserRequestDto;
import com.example.adminService.Dto.UserBasicInfoDto;
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
     * Uppdatera användarinformation
     */
    UserBasicInfoDto updateUserInfo(Long userId, UpdateUserRequestDto updateRequest);
}
