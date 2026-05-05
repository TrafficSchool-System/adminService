package com.example.adminService.features.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for manual payment creation
 * Received from PaymentService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPaymentResponse {
    private String id;
    private Long userId;
    private Long packageId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private String packageName; // Added for convenience
}
