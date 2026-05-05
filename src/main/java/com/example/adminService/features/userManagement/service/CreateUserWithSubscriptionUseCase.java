package com.example.adminService.features.userManagement.service;

import com.example.adminService.features.userManagement.dto.CreateUserWithSubscriptionDTO;
import com.example.adminService.features.userManagement.dto.ManualPaymentRequest;
import com.example.adminService.features.userManagement.dto.ManualPaymentResponse;
import com.example.adminService.features.userManagement.dto.UserBasicInfoDto;
import com.example.adminService.features.userManagement.dto.UserWithSubscriptionResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ==========================================
 * USE CASE: CREATE USER WITH SUBSCRIPTION
 * ==========================================
 * Ansvar: Orchestrera skapande av user + tilldelning av subscription
 * 
 * ORCHESTRATION FLOW:
 * 1. Skapa user i UserService (skickar magic link automatiskt)
 * 2. Skapa manual payment i PaymentService (aktiverar subscription automatiskt)
 * 3. Returnera aggregerad response med user + subscription info
 * 
 * SERVICE-TO-SERVICE KOMMUNIKATION:
 * AdminService → UserService (POST /api/admin/users)
 * AdminService → PaymentService (POST /api/admin/payments/manual)
 * 
 * TRANSAKTIONSHANTERING:
 * Detta är en distribuerad transaktion över 2 services.
 * Om PaymentService misslyckas har vi redan skapat user i UserService.
 * User får fortfarande magic link men ingen aktiv subscription.
 * Admin kan manuellt tilldela subscription senare.
 * 
 * BUSINESS LOGIC:
 * Admin skapar user och betalar för subscription åt användaren.
 * User får:
 * - Konto skapat
 * - Magic link för lösenord
 * - Aktiv subscription (om payment lyckas)
 * 
 * ANVÄNDS AV: UserManagementController.createUserWithSubscription()
 * 
 * @author Senior Java Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserWithSubscriptionUseCase {

    private final WebClient userServiceWebClient;
    private final WebClient paymentServiceWebClient;

    @Value("${service.api.key}")
    private String serviceApiKey;

    /**
     * Skapa user och tilldela subscription i ett orchestrerat flöde
     * 
     * @param request User info + package ID
     * @return UserWithSubscriptionResponseDTO med status och info
     */
    public UserWithSubscriptionResponseDTO execute(CreateUserWithSubscriptionDTO request) {
        log.info("Creating user with subscription: packageId={}", request.getPackageId());

        try {
            // STEG 1: Skapa user i UserService (skickar magic link)
            log.debug("📝 Step 1: Creating user in UserService...");
            UserBasicInfoDto user = createUserInUserService(request);
            log.info("✅ User created: id={}", user.getId());

            // STEG 2: Skapa manual payment i PaymentService (aktiverar subscription)
            log.debug("💳 Step 2: Creating manual payment in PaymentService...");
            ManualPaymentResponse payment = createManualPayment(user.getId(), request.getPackageId());
            log.info("✅ Manual payment created: id={}, package={}",
                    payment.getId(), payment.getPackageName());

            // STEG 3: Bygg success response
            UserWithSubscriptionResponseDTO response = UserWithSubscriptionResponseDTO.builder()
                    .user(user)
                    .paymentId(payment.getId())
                    .packageName(payment.getPackageName())
                    .packagePrice(payment.getAmount())
                    .status("SUCCESS")
                    .message("User created with active subscription. Magic link sent.")
                    .build();

            log.info("✅ User with subscription created successfully: userId={}", user.getId());

            return response;

        } catch (Exception e) {
            log.error("❌ Failed to create user with subscription: {}", e.getMessage(), e);

            // Returnera error response
            return UserWithSubscriptionResponseDTO.builder()
                    .status("FAILED")
                    .message("Failed to create user: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Skapa user i UserService
     * 
     * @throws RuntimeException om user creation misslyckas
     */
    private UserBasicInfoDto createUserInUserService(CreateUserWithSubscriptionDTO request) {
        try {
            UserBasicInfoDto user = userServiceWebClient
                    .post()
                    .uri("/api/admin/users")
                    .header("X-Internal-API-Key", serviceApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UserBasicInfoDto.class)
                    .block();

            return user;
        } catch (Exception e) {
            log.error("❌ Failed to create user in UserService: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    /**
     * Skapa manual payment i PaymentService (aktiverar subscription automatiskt)
     * 
     * @throws RuntimeException om payment creation misslyckas
     */
    private ManualPaymentResponse createManualPayment(Long userId, Long packageId) {
        try {
            ManualPaymentRequest paymentRequest = ManualPaymentRequest.builder()
                    .userId(userId)
                    .packageId(packageId)
                    .build();

            ManualPaymentResponse payment = paymentServiceWebClient
                    .post()
                    .uri("/api/admin/payments/manual")
                    .header("X-Internal-API-Key", serviceApiKey)
                    .bodyValue(paymentRequest)
                    .retrieve()
                    .bodyToMono(ManualPaymentResponse.class)
                    .block();

            return payment;
        } catch (Exception e) {
            log.error("❌ Failed to create manual payment in PaymentService: userId={}, error={}",
                    userId, e.getMessage());
            throw new RuntimeException("Failed to create subscription: " + e.getMessage(), e);
        }
    }
}
