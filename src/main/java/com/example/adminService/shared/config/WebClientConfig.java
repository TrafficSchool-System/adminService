package com.example.adminService.shared.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

/**
 * ==========================================
 * WEBCLIENT CONFIGURATION
 * ==========================================
 * Konfigurerar WebClient för service-to-service kommunikation.
 *
 * VIKTIGT:
 * - Använder Azure Container Apps intern DNS direkt (http://user-service, etc.)
 * - @LoadBalanced/Eureka används INTE — samma approach som API-gatewayen
 * - 8s response timeout förhindrar hängande anrop
 */
@Configuration
public class WebClientConfig {

    @Value("${service.api.key}")
    private String serviceApiKey;

    private WebClient buildClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(8));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("X-Internal-Source", "admin-service")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .build();
    }

    @Bean
    public WebClient userServiceWebClient() {
        return buildClient("http://user-service");
    }

    @Bean
    public WebClient paymentServiceWebClient() {
        return buildClient("http://payment-service");
    }

    @Bean
    public WebClient quizServiceWebClient() {
        return buildClient("http://quiz-service");
    }

    @Bean
    public WebClient examServiceWebClient() {
        return buildClient("http://exam-service");
    }
}
