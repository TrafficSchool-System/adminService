package com.example.adminService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ==========================================
 * COMPLETE USER DETAILS DTO
 * ==========================================
 * Aggregerar ALL information om en användare från olika microservices
 * Används i admin-gränssnittet för att visa komplett användaröversikt
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUserDetailsDto {

    // Användarinformation från UserService
    private UserBasicInfoDto userInfo;

    // Prenumerationer från PaymentService
    private List<SubscriptionInfoDto> subscriptions;

    // Betalningar från PaymentService
    private List<PaymentInfoDto> payments;

    // Beräknad statistik
    private UserStatisticsDto statistics;
}
