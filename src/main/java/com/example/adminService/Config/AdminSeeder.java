package com.example.adminService.Config;

import com.example.adminService.Entity.Admin;
import com.example.adminService.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder {

    @Bean
    CommandLineRunner initAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.findByUsername("admin").isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@trafficschool.com");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setActive(true);

                adminRepository.save(admin);
                System.out.println("✅ Default admin user created: admin / admin123");
            }
        };
    }
}