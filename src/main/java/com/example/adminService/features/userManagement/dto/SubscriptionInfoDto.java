package com.example.adminService.features.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO för prenumerationsinformation från UserService
 * Matchar SubscriptionResponseDTO från UserService
 * 
 * CLEANAD VERSION - Dubbletter borttagna:
 * ❌ Borttaget: price (dubblett av packagePrice)
 * ❌ Borttaget: valid (kan beräknas: active && !isExpired)
 * ❌ Borttaget: isExpired (kan beräknas från endDate)
 * ❌ Borttaget: isExpiringSoon (kan beräknas från daysRemaining)
 * 
 * VIKTIG DATA FÖR ADMIN:
 * - paymentId: Kopplar prenumeration till specifik betalning
 * - purchaseDate: När betalningen gjordes (kan skilja sig från startDate)
 * - hoursRemaining: Mer exakt än daysRemaining för admin-tracking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionInfoDto {
    private Long id;
    private Long userId;
    private Long packageId;
    private String packageName;
    private BigDecimal packagePrice;
    private Integer validityDays;
    private Integer validityHours;
    private String paymentId;
    private LocalDateTime purchaseDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private Long hoursRemaining;
    private Long daysRemaining;
}
