package com.example.adminService.shared.config;

import com.example.adminService.features.auth.entity.Admin;
import com.example.adminService.features.auth.repository.AdminRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${admin.default.username:admin}")
    private String defaultUsername;

    @Value("${admin.default.password:admin123}")
    private String defaultPassword;

    @Value("${admin.default.email:admin@trafficschool.com}")
    private String defaultEmail;

    @Bean
    CommandLineRunner initAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.findByUsername(defaultUsername).isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername(defaultUsername);
                admin.setPassword(passwordEncoder.encode(defaultPassword));
                admin.setEmail(defaultEmail);
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setActive(true);

                adminRepository.save(admin);
                log.info("Default admin user created - username: {}", admin.getUsername());
            }
        };
    }
}