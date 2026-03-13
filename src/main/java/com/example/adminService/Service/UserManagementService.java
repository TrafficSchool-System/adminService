package com.example.adminService.Service;

import com.example.adminService.Dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserManagementServiceInterface {

    private final WebClient userServiceWebClient;
    private final WebClient paymentServiceWebClient;

    @Value("${service.api.key}")
    private String serviceApiKey;

    @Override
    public CompleteUserDetailsDto getCompleteUserDetails(Long userId) {
        UserBasicInfoDto userInfo = getUserInfo(userId);
        List<SubscriptionInfoDto> subscriptions = getUserSubscriptions(userId);
        List<PaymentInfoDto> payments = getUserPayments(userId);
        UserStatisticsDto statistics = calculateStatistics(subscriptions, payments);
        boolean hasActiveSubscription = subscriptions.stream()
                .anyMatch(SubscriptionInfoDto::getActive);
        userInfo.setIsActive(hasActiveSubscription);
        CompleteUserDetailsDto completeDetails = new CompleteUserDetailsDto();
        completeDetails.setUserInfo(userInfo);
        completeDetails.setSubscriptions(subscriptions);
        completeDetails.setPayments(payments);
        completeDetails.setStatistics(statistics);
        return completeDetails;
    }

    @Override
    public List<CompleteUserDetailsDto> getAllUsersWithDetails() {
        List<UserBasicInfoDto> allUsers = getAllUsers();
        return allUsers.stream()
                .map(user -> getCompleteUserDetails(user.getId()))
                .collect(Collectors.toList());
    }

    private UserBasicInfoDto getUserInfo(Long userId) {
        try {
            UserBasicInfoDto user = userServiceWebClient
                    .get()
                    .uri("/api/admin/users/{id}", userId)
                    .header("X-Internal-API-Key", serviceApiKey)
                    .retrieve()
                    .bodyToMono(UserBasicInfoDto.class)
                    .block();
            return user;
        } catch (Exception e) {
            UserBasicInfoDto fallback = new UserBasicInfoDto();
            fallback.setId(userId);
            fallback.setEmail("Användarinfo ej tillgänglig");
            return fallback;
        }
    }

    private List<UserBasicInfoDto> getAllUsers() {
        try {
            List<UserBasicInfoDto> users = userServiceWebClient
                    .get()
                    .uri("/api/admin/users")
                    .header("X-Internal-API-Key", serviceApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UserBasicInfoDto>>() {
                    })
                    .block();
            return users != null ? users : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<SubscriptionInfoDto> getUserSubscriptions(Long userId) {
        try {
            return userServiceWebClient
                    .get()
                    .uri("/api/subscriptions/user/{userId}", userId)
                    .header("X-Internal-API-Key", serviceApiKey)
                    .header("X-User-Role", "ADMIN")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SubscriptionInfoDto>>() {
                    })
                    .block();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<PaymentInfoDto> getUserPayments(Long userId) {
        try {
            return paymentServiceWebClient
                    .get()
                    .uri("/api/admin/payments/users/{userId}/payments", userId)
                    .header("X-Internal-API-Key", serviceApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PaymentInfoDto>>() {
                    })
                    .block();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private UserStatisticsDto calculateStatistics(
            List<SubscriptionInfoDto> subscriptions,
            List<PaymentInfoDto> payments) {
        UserStatisticsDto stats = new UserStatisticsDto();
        stats.setTotalSubscriptions(subscriptions.size());
        stats.setActiveSubscriptions((int) subscriptions.stream()
                .filter(SubscriptionInfoDto::getActive)
                .count());
        stats.setExpiredSubscriptions((int) subscriptions.stream()
                .filter(SubscriptionInfoDto::getIsExpired)
                .count());
        stats.setHasActiveSubscription(stats.getActiveSubscriptions() > 0);

        // Beräkna närmaste utgångsdatum från aktiva prenumerationer
        stats.setNearestExpirationDate(
                subscriptions.stream()
                        .filter(SubscriptionInfoDto::getActive)
                        .map(SubscriptionInfoDto::getEndDate)
                        .filter(endDate -> endDate != null)
                        .min(java.time.LocalDateTime::compareTo)
                        .orElse(null));

        stats.setTotalPayments(payments.size());
        stats.setSuccessfulPayments((int) payments.stream()
                .filter(p -> "PAID".equals(p.getStatus()))
                .count());
        stats.setFailedPayments((int) payments.stream()
                .filter(p -> "FAILED".equals(p.getStatus()))
                .count());
        BigDecimal totalSpent = payments.stream()
                .filter(p -> "PAID".equals(p.getStatus()))
                .map(PaymentInfoDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSpent(totalSpent);
        return stats;
    }

    @Override
    public UserBasicInfoDto updateUserInfo(Long userId, UpdateUserRequestDto updateRequest) {
        try {
            UserBasicInfoDto updatedUser = userServiceWebClient
                    .put()
                    .uri("/api/admin/users/{id}", userId)
                    .header("X-Internal-API-Key", serviceApiKey)
                    .bodyValue(updateRequest)
                    .retrieve()
                    .bodyToMono(UserBasicInfoDto.class)
                    .block();
            return updatedUser;
        } catch (Exception e) {
            throw new RuntimeException("Kunde inte uppdatera användare: " + e.getMessage());
        }
    }

}
