package com.example.adminService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating manual payment
 * Used by AdminService when calling PaymentService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPaymentRequest {
    private Long userId;
    private Long packageId;
}
