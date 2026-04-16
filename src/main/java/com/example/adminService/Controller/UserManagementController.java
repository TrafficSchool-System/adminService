package com.example.adminService.Controller;

import com.example.adminService.Dto.CompleteUserDetailsDto;
import com.example.adminService.Dto.CreateUserWithSubscriptionDTO;
import com.example.adminService.Dto.UpdateUserRequestDto;
import com.example.adminService.Dto.UserBasicInfoDto;
import com.example.adminService.Dto.UserWithSubscriptionResponseDTO;
import com.example.adminService.Service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * ADMIN USER AGGREGATION CONTROLLER
 * 
 * RESTful endpoints for aggregated user data from multiple services.
 * Base path: /api/admin/users
 * 
 * ARCHITECTURE:
 * Frontend → Gateway → AdminService → UserService + PaymentService + others
 * 
 * PURPOSE:
 * - Aggregate user data + subscriptions + payments in ONE call
 * - Reduce frontend requests (from 3 to 1)
 * - Centralized admin logic for complex data handling
 * 
 * AGGREGATION ENDPOINTS:
 * - GET /admin/users : List all users with aggregated data
 * - GET /admin/users/{id} : Get complete user details
 * - PUT /admin/users/{id} : Update user information
 * 
 * NOTE:
 * For simple data (user only), individual services can be used directly.
 * These endpoints are for complex aggregated views.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * GET COMPLETE USER DETAILS
     * GET /api/admin/users/{userId}
     * 
     * Returns aggregated user information from multiple services:
     * - User profile (from UserService)
     * - Subscriptions (from UserService)
     * - Payments (from PaymentService)
     * - Calculated statistics
     * 
     * @param userId User ID to retrieve
     * @return Complete user details with aggregated data
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CompleteUserDetailsDto> getCompleteUserDetails(
            @PathVariable Long userId) {

        CompleteUserDetailsDto details = userManagementService.getCompleteUserDetails(userId);

        if (details.getUserInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(details);
    }

    /**
     * LIST ALL USERS WITH AGGREGATED DATA
     * GET /api/admin/users
     * 
     * Returns list of all users with complete information:
     * - User profile
     * - Subscriptions
     * - Payments
     * - Statistics
     * 
     * NOTE: This endpoint may take longer if there are many users.
     * Consider pagination for large datasets.
     * 
     * @return List of all users with aggregated details
     */
    @GetMapping
    public ResponseEntity<List<CompleteUserDetailsDto>> getAllUsersWithDetails() {

        List<CompleteUserDetailsDto> allUsers = userManagementService.getAllUsersWithDetails();

        return ResponseEntity.ok(allUsers);
    }

    /**
     * CREATE USER WITH SUBSCRIPTION (ADMIN)
     * POST /api/admin/users
     * 
     * Creates a new user + assigns package subscription in one operation.
     * Used when admin sells package manually (in person, over phone, etc.).
     * 
     * FLOW:
     * 1. Create user in UserService (sends magic link email)
     * 2. Create manual payment in PaymentService (status: PAID, method: MANUAL)
     * 3. PaymentService activates subscription in UserService
     * 
     * @param request User info + package ID
     * @return Created user with subscription details
     */
    @PostMapping
    public ResponseEntity<UserWithSubscriptionResponseDTO> createUserWithSubscription(
            @Valid @RequestBody CreateUserWithSubscriptionDTO request) {

        UserWithSubscriptionResponseDTO response = userManagementService.createUserWithSubscription(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UPDATE USER INFORMATION
     * PUT /api/admin/users/{userId}
     * 
     * Updates user profile information.
     * Delegates to UserService for actual update.
     * 
     * @param userId        User ID to update
     * @param updateRequest Updated user information
     * @return Updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserBasicInfoDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDto updateRequest) {

        UserBasicInfoDto updatedUser = userManagementService.updateUserInfo(userId, updateRequest);

        return ResponseEntity.ok(updatedUser);
    }

}
