package com.example.adminService.Security;

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

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Vår egna JWT filter som validerar token

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Stänger av CSRF-Skydd eftersom vi använder JWT (stateless)
                .csrf(csrf -> csrf.disable())

                // Konfigurera vilka endpoints som kräver authentication
                .authorizeHttpRequests(auth -> auth

                        // Öppna Endpoints
                        .requestMatchers("api/admin/auth/login").permitAll()

                        // Alla andra kräver också authentication
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Alla andra blockera
                        .anyRequest().denyAll()
                )
                
                // Konfigurera stateless session (vi lagrar ingen session på servern)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Lägg till vårt JWT-filter innan standard Spring Security authenticationfilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Hantering av fel relaterade till autentication/authorization
                .exceptionHandling(exceptions -> exceptions

                        // Hantera 401 Unauthorized (token saknas eller ogiltig)
                        .authenticationEntryPoint((request, response, authException) -> {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write(
                                        "{\"error\": \"Unauthorized\", \"message\": \"Token krävs för denna endpoint\"}"
                                );
                        })

                        // Hantera 403 Forbidden (användare har inte rätt roll)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.setStatus(403);
                                response.setContentType("application/json"); 
                                response.getWriter().write(
                                        "{\"error\": \"Forbidden\", \"message\": \"Du har inte behörighet att komma åt denna resurs\"}"
                                );
                                
                        }));
                        
                return http.build(); // Retunera den färdiga SecurityFilterChain-objektet till Sptring 
        
    }
}
