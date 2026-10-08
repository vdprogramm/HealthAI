package com.vinh.healthcare.config;

import com.vinh.healthcare.entity.Doctor;
import com.vinh.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@Profile("dev")
public class DoctorInitializer {

        @Bean
        CommandLineRunner seedDoctors(
                        DoctorRepository doctorRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${DOCTOR_PASSWORD:123456}") String productionPassword,
                        @Value("${spring.profiles.active:default}") String profile) {
                return args -> {
                        boolean development = profile.equals("dev");

                        String password = development
                                        ? "123456"
                                        : productionPassword;

                        if (password == null || password.isBlank()) {
                                throw new IllegalStateException(
                                                "Thiếu DOCTOR_PASSWORD cho môi trường triển khai");
                        }

                        createDoctor(doctorRepository, passwordEncoder,
                                        "doctor1@healthai.com", "Nguyễn Văn Minh",
                                        "Nội tổng quát", "200000", password);

                        createDoctor(doctorRepository, passwordEncoder,
                                        "doctor2@healthai.com", "Trần Thị Lan",
                                        "Tim mạch", "300000", password);

                        createDoctor(doctorRepository, passwordEncoder,
                                        "doctor3@healthai.com", "Lê Quốc Huy",
                                        "Thần kinh", "350000", password);

                        createDoctor(doctorRepository, passwordEncoder,
                                        "doctor4@healthai.com", "Phạm Thu Hà",
                                        "Da liễu", "250000", password);

                        createDoctor(doctorRepository, passwordEncoder,
                                        "doctor5@healthai.com", "Hoàng Đức Anh",
                                        "Nhi khoa", "220000", password);
                };
        }

        private void createDoctor(
                        DoctorRepository repository,
                        PasswordEncoder encoder,
                        String email,
                        String name,
                        String specialty,
                        String price,
                        String password) {
                if (repository.findByEmail(email).isPresent()) {
                        return;
                }

                Doctor doctor = Doctor.builder()
                                .email(email)
                                .matKhau(encoder.encode(password))
                                .tenBacSi(name)
                                .chuyenKhoa(specialty)
                                .giaKham(new BigDecimal(price))
                                .build();

                repository.save(doctor);
        }
}
