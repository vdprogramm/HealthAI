package com.vinh.healthcare.config;

import com.vinh.healthcare.entity.Admin;
import com.vinh.healthcare.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createDefaultAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_PASSWORD:123456}") String adminPassword) {
        return args -> {
            String email = "admin@healthai.com";

            if (adminRepository.findByEmail(email).isEmpty()) {
                Admin admin = Admin.builder()
                        .hoTen("HealthAI Administrator")
                        .email(email)
                        .matKhau(passwordEncoder.encode(adminPassword))
                        .build();

                adminRepository.save(admin);
                System.out.println("Default admin account initialized");
            }
        };
    }
}
