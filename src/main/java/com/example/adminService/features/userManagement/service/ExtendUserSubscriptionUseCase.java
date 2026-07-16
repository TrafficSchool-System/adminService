package com.example.adminService.features.userManagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * EXTEND USER SUBSCRIPTION USE CASE
 *
 * Orchestrerar förlängning av en elevs prenumeration.
 * Anropar userService internt.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExtendUserSubscriptionUseCase {

    private final WebClient userServiceWebClient;

    @Value("${service.api.key}")
    private String serviceApiKey;

    public void execute(Long userId, Integer days) {
        log.info("Extending subscription for userId={} by {} days", userId, days);

        try {
            userServiceWebClient
                    .put()
                    .uri("/api/subscriptions/extend")
                    .header("X-Internal-API-Key", serviceApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("userId", userId, "days", days))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Subscription extended for userId={}", userId);
        } catch (WebClientResponseException e) {
            log.error("UserService returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to extend subscription: " + e.getResponseBodyAsString(), e);
        }
    }
}