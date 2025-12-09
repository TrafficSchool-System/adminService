package com.example.adminService.Filter;

import com.example.adminService.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // STEG 1: Extrahera Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // STEG 2: Kolla om header finns och börjar med "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // STEG 3: Extrahera JWT token (ta bort "Bearer " prefix)
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // STEG 4: Om username finns och ingen authentication är satt än
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // STEG 5: Ladda admin från database via UserDetailsService
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // STEG 6: Validera token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // STEG 7: Skapa authentication object
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities() // ROLE_ADMIN
                    );
                
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // STEG 8: Sätt authentication i Security Context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // STEG 9: Fortsätt filter chain
        filterChain.doFilter(request, response);
    }
}