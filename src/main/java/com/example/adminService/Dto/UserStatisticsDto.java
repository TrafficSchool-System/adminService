package com.example.adminService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO för beräknad användarstatistik
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDto {
    private Integer totalSubscriptions;
    private Integer activeSubscriptions;
    private Integer expiredSubscriptions;
    private Integer totalPayments;
    private Integer successfulPayments;
    private Integer failedPayments;
    private BigDecimal totalSpent;
    private Boolean hasActiveSubscription;
    private LocalDateTime nearestExpirationDate; // Närmaste utgångsdatum för aktiv prenumeration
}
