package com.example.adminService.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ==========================================
 * WEBCLIENT CONFIGURATION
 * ==========================================
 * Konfigurerar load-balanced WebClient för service-to-service kommunikation.
 * 
 * VIKTIGT:
 * - @LoadBalanced aktiverar Eureka service discovery
 * - Service namn (http://user-service) översätts automatiskt till IP:PORT via
 * Eureka
 * - WebClient.Builder är thread-safe och kan återanvändas
 * 
 * ANVÄNDNING:
 * - AdminService anropar andra microservices via WebClient
 * - Inga direkta HTTP-anrop till localhost:PORT
 * - Eureka hanterar automatisk service discovery och load balancing
 */
@Configuration
public class WebClientConfig {

    @Value("${service.api.key}")
    private String serviceApiKey;

    /**
     * WebClient.Builder med load balancing
     * Används som base för alla service-specifika WebClients
     * 
     * @LoadBalanced: Aktiverar Eureka service discovery
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /**
     * WebClient för UserService
     * Base URL: http://user-service (resolveras via Eureka till ex: localhost:8081)
     * 
     * ANVÄNDS AV: UserManagementService
     * 
     * Aggregerar användardata från UserService för admin-panelen:
     * - GET /api/users → Hämta alla användare (för aggregering)
     * - GET /api/users/{id} → Hämta användare (för aggregering)
     * 
     * OBS: AdminService anropar INTE UserService som proxy längre.
     * Frontend kan anropa UserService direkt via Gateway (med ADMIN JWT).
     * AdminService används endast för AGGREGERING av data från flera services.
     */
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://user-service")
                .defaultHeader("X-Internal-Source", "admin-service")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .build();
    }

    /**
     * WebClient för PaymentService
     * Base URL: http://payment-service (resolveras via Eureka)
     * 
     * Endpoints som anropas:
     * - GET /api/payments → Hämta alla betalningar
     * - GET /api/payments/{id} → Hämta betalning
     * - GET /api/payments/user/{userId} → Hämta användarens betalningar
     * - POST /api/packages → Skapa paket
     * - PUT /api/packages/{id} → Uppdatera paket
     * - DELETE /api/packages/{id} → Ta bort paket
     */
    @Bean
    public WebClient paymentServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://payment-service")
                .defaultHeader("X-Internal-Source", "admin-service")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .build();
    }

    /**
     * WebClient för QuizService
     * Base URL: http://quiz-service (resolveras via Eureka)
     * 
     * Endpoints som anropas:
     * - POST /api/quizzes/import → Importera frågor från Excel
     * - GET /api/quizzes/files → Hämta Excel-filer
     * - DELETE /api/quizzes/files/{id} → Ta bort Excel-fil
     * - GET /api/quizzes/{id} → Hämta fråga
     * - GET /api/quizzes → Hämta alla frågor
     * - PUT /api/quizzes/{id} → Uppdatera fråga
     */
    @Bean
    public WebClient quizServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://quiz-service")
                .defaultHeader("X-Internal-Source", "admin-service")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .build();
    }

    /**
     * WebClient för ExamService
     * Base URL: http://exam-service (resolveras via Eureka)
     * 
     * Endpoints som anropas:
     * - GET /api/exams/results/{userId} → Hämta användarens examresultat
     * - GET /api/exams/counts → Hämta exam-statistik
     */
    @Bean
    public WebClient examServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://exam-service")
                .defaultHeader("X-Internal-Source", "admin-service")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .build();
    }
}
