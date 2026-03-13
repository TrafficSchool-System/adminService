package com.example.adminService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO för prenumerationsinformation från UserService
 * Matchar SubscriptionResponseDTO från UserService
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
    private Long id; // Subscription ID (för teknisk referens)
    private Long userId; // User ID (för koppling)
    private Long packageId; // Package ID (teknisk referens)
    private String packageName; // Namnet på paketet
    private BigDecimal price; // Priset vid köptillfället (alias för packagePrice)
    private BigDecimal packagePrice; // Original pris från backend
    private Integer validityDays; // Total längd i dagar
    private Integer validityHours; // Total längd i timmar (mer exakt)
    private String paymentId; // Koppling till Payment (VIKTIGT!)
    private LocalDateTime purchaseDate; // När betalningen gjordes (VIKTIGT!)
    private LocalDateTime startDate; // När prenumerationen startade
    private LocalDateTime endDate; // När prenumerationen slutar
    private Boolean active; // Om prenumerationen är aktiv
    private Long hoursRemaining; // Timmar kvar (mer exakt än dagar)
    private Long daysRemaining; // Dagar kvar
    private Boolean isExpired; // Om prenumerationen har gått ut
    private Boolean valid; // Om prenumerationen är giltig (active && !expired)
    private Boolean isExpiringSoon; // Om prenumerationen går ut inom 3 dagar
}
