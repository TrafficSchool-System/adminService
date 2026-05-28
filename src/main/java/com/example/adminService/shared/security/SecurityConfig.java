package com.example.adminService.shared.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Aktivera Spring Security
@EnableMethodSecurity // Aktivera PreAuthorize och andra metoder
public class SecurityConfig {

        /**
         * REFACTORED SECURITY ARCHITECTURE:
         * - Gateway validerar JWT (JwtAuthenticationGlobalFilter)
         * - Gateway sätter headers: X-User-Id, X-User-Email, X-User-Role
         * - AdminService läser headers (GatewayHeaderAuthenticationFilter)
         */
        @Autowired
        private GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Stänger av CSRF-Skydd eftersom vi använder JWT (stateless)
                                .csrf(csrf -> csrf.disable())

                                // Configure which endpoints require authentication
                                // ORDER IMPORTANT: More specific rules must come first!
                                .authorizeHttpRequests(auth -> auth

                                                // ==============================================
                                                // PUBLIC ENDPOINTS - No authentication required
                                                // ==============================================
                                                .requestMatchers(
                                                                "/api/admin/auth/login",
                                                                "/api/admin/auth/health",
                                                                "/actuator/health",
                                                                "/actuator/info")
                                                .permitAll()

                                                // ==============================================
                                                // ADMIN ENDPOINTS - Require ADMIN role
                                                // ==============================================
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                // All others deny
                                                .anyRequest().denyAll())

                                // Konfigurera stateless session (vi lagrar ingen session på servern)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Lägg till Gateway header filter som läser X-User-* headers
                                .addFilterBefore(gatewayHeaderAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                // Hantering av fel relaterade till autentication/authorization
                                .exceptionHandling(exceptions -> exceptions

                                                // Hantera 401 Unauthorized (token saknas eller ogiltig)
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(401);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Unauthorized\", \"message\": \"Token krävs för denna endpoint\"}");
                                                })

                                                // Hantera 403 Forbidden (användare har inte rätt roll)
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(403);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Forbidden\", \"message\": \"Du har inte behörighet att komma åt denna resurs\"}");

                                                }));

                return http.build(); // Retunera den färdiga SecurityFilterChain-objektet till Sptring

        }
}
