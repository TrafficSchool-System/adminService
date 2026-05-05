package com.example.adminService.features.userManagement.service;

import com.example.adminService.features.userManagement.dto.CompleteUserDetailsDto;
import com.example.adminService.features.userManagement.dto.PaymentInfoDto;
import com.example.adminService.features.userManagement.dto.SubscriptionInfoDto;
import com.example.adminService.features.userManagement.dto.UserBasicInfoDto;
import com.example.adminService.features.userManagement.dto.UserStatisticsDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ==========================================
 * USE CASE: GET COMPLETE USER DETAILS
 * ==========================================
 * Ansvar: Aggregera komplett användarinformation från flera microservices
 * 
 * DATA AGGREGATION:
 * - UserService: Grundläggande user-info (email, namn, etc.)
 * - UserService: Subscriptions (aktiva/utgångna prenumerationer)
 * - PaymentService: Payments (betalningshistorik)
 * - Intern beräkning: Statistics (totalSpent, activeSubscriptions, etc.)
 * 
 * BUSINESS LOGIC:
 * 1. Hämta user basic info från UserService
 * 2. Hämta user subscriptions från UserService
 * 3. Hämta user payments från PaymentService
 * 4. Beräkna statistik (total spent, active subscriptions, etc.)
 * 5. Sätt isActive baserat på om user har aktiv subscription
 * 6. Returnera aggregerad CompleteUserDetailsDto
 * 
 * ANVÄNDS AV: UserManagementController.getUserDetails()
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetCompleteUserDetailsUseCase {

        private final WebClient userServiceWebClient;
        private final WebClient paymentServiceWebClient;

        @Value("${service.api.key}")
        private String serviceApiKey;

        /**
         * Hämta komplett användarinformation med aggregerad data
         * 
         * @param userId User ID
         * @return CompleteUserDetailsDto med user, subscriptions, payments och
         *         statistics
         */
        public CompleteUserDetailsDto execute(Long userId) {
                log.info("📊 Fetching complete user details: userId={}", userId);

                // STEG 1: Hämta user basic info
                UserBasicInfoDto userInfo = fetchUserInfo(userId);

                // STEG 2: Hämta subscriptions
                List<SubscriptionInfoDto> subscriptions = fetchUserSubscriptions(userId);

                // STEG 3: Hämta payments
                List<PaymentInfoDto> payments = fetchUserPayments(userId);

                // STEG 4: Beräkna statistik
                UserStatisticsDto statistics = calculateStatistics(subscriptions, payments);

                // STEG 5: Sätt isActive baserat på subscriptions
                boolean hasActiveSubscription = subscriptions.stream()
                                .anyMatch(SubscriptionInfoDto::getActive);
                userInfo.setIsActive(hasActiveSubscription);

                // STEG 6: Bygg aggregerad response
                CompleteUserDetailsDto completeDetails = new CompleteUserDetailsDto();
                completeDetails.setUserInfo(userInfo);
                completeDetails.setSubscriptions(subscriptions);
                completeDetails.setPayments(payments);
                completeDetails.setStatistics(statistics);

                log.info("✅ Complete user details fetched successfully: userId={}, activeSubscriptions={}, totalPayments={}",
                                userId, statistics.getActiveSubscriptions(), statistics.getTotalPayments());

                return completeDetails;
        }

        /**
         * Hämta user basic info från UserService
         */
        private UserBasicInfoDto fetchUserInfo(Long userId) {
                try {
                        log.debug("🔄 Fetching user info from UserService: userId={}", userId);
                        UserBasicInfoDto user = userServiceWebClient
                                        .get()
                                        .uri("/api/admin/users/{id}", userId)
                                        .header("X-Internal-API-Key", serviceApiKey)
                                        .retrieve()
                                        .bodyToMono(UserBasicInfoDto.class)
                                        .block();

                        log.debug("✅ User info fetched: userId={}", userId);
                        return user;
                } catch (Exception e) {
                        log.error("❌ Failed to fetch user info: userId={}, error={}", userId, e.getMessage());
                        // Returnera fallback med minimal info
                        UserBasicInfoDto fallback = new UserBasicInfoDto();
                        fallback.setId(userId);
                        fallback.setEmail("Användarinfo ej tillgänglig");
                        return fallback;
                }
        }

        /**
         * Hämta user subscriptions från UserService
         */
        private List<SubscriptionInfoDto> fetchUserSubscriptions(Long userId) {
                try {
                        log.debug("🔄 Fetching user subscriptions from UserService: userId={}", userId);
                        List<SubscriptionInfoDto> subscriptions = userServiceWebClient
                                        .get()
                                        .uri("/api/subscriptions/user/{userId}", userId)
                                        .header("X-Internal-API-Key", serviceApiKey)
                                        .header("X-User-Role", "ADMIN")
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<SubscriptionInfoDto>>() {
                                        })
                                        .block();

                        log.debug("✅ Subscriptions fetched: count={}",
                                        subscriptions != null ? subscriptions.size() : 0);
                        return subscriptions != null ? subscriptions : new ArrayList<>();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch user subscriptions: userId={}, error={}", userId, e.getMessage());
                        return new ArrayList<>();
                }
        }

        /**
         * Hämta user payments från PaymentService
         */
        private List<PaymentInfoDto> fetchUserPayments(Long userId) {
                try {
                        log.debug("🔄 Fetching user payments from PaymentService: userId={}", userId);
                        List<PaymentInfoDto> payments = paymentServiceWebClient
                                        .get()
                                        .uri("/api/admin/payments/users/{userId}/payments", userId)
                                        .header("X-Internal-API-Key", serviceApiKey)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<PaymentInfoDto>>() {
                                        })
                                        .block();

                        log.debug("✅ Payments fetched: count={}", payments != null ? payments.size() : 0);
                        return payments != null ? payments : new ArrayList<>();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch user payments: userId={}, error={}", userId, e.getMessage());
                        return new ArrayList<>();
                }
        }

        /**
         * Beräkna användarstatistik från subscriptions och payments
         */
        private UserStatisticsDto calculateStatistics(
                        List<SubscriptionInfoDto> subscriptions,
                        List<PaymentInfoDto> payments) {

                log.debug("📊 Calculating user statistics...");

                UserStatisticsDto stats = new UserStatisticsDto();

                // Subscription statistics
                stats.setTotalSubscriptions(subscriptions.size());
                stats.setActiveSubscriptions((int) subscriptions.stream()
                                .filter(SubscriptionInfoDto::getActive)
                                .count());
                stats.setExpiredSubscriptions((int) subscriptions.stream()
                                .filter(s -> !s.getActive())
                                .count());
                stats.setHasActiveSubscription(stats.getActiveSubscriptions() > 0);

                // Närmaste utgångsdatum från aktiva prenumerationer
                stats.setNearestExpirationDate(
                                subscriptions.stream()
                                                .filter(SubscriptionInfoDto::getActive)
                                                .map(SubscriptionInfoDto::getEndDate)
                                                .filter(endDate -> endDate != null)
                                                .min(java.time.LocalDateTime::compareTo)
                                                .orElse(null));

                // Payment statistics
                stats.setTotalPayments(payments.size());
                stats.setSuccessfulPayments((int) payments.stream()
                                .filter(p -> "PAID".equals(p.getStatus()))
                                .count());
                stats.setFailedPayments((int) payments.stream()
                                .filter(p -> "FAILED".equals(p.getStatus()))
                                .count());

                // Total spent (summa av alla PAID payments)
                BigDecimal totalSpent = payments.stream()
                                .filter(p -> "PAID".equals(p.getStatus()))
                                .map(PaymentInfoDto::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.setTotalSpent(totalSpent);

                log.debug("✅ Statistics calculated: activeSubscriptions={}, totalSpent={}",
                                stats.getActiveSubscriptions(), stats.getTotalSpent());

                return stats;
        }
}
