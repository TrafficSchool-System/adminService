package com.example.adminService.features.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for admin user creation with subscription
 * 
 * Returns both user and payment information after successful creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithSubscriptionResponseDTO {

    private UserBasicInfoDto user;
    private String paymentId;
    private String packageName;
    private Double packagePrice;
    private String status; // "SUCCESS" or "FAILED"
    private String message;
}
