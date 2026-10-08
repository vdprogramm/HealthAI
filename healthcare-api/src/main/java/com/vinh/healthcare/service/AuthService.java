package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.AuthResponse;
import com.vinh.healthcare.dto.LoginRequest;
import com.vinh.healthcare.dto.RegisterRequest;
import com.vinh.healthcare.entity.Patient;
import com.vinh.healthcare.repository.PatientRepository;
import com.vinh.healthcare.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (patientRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        Patient patient = Patient.builder()
                .hoTen(request.hoTen())
                .email(request.email())
                .matKhau(
                        passwordEncoder.encode(request.matKhau())
                )
                .soDienThoai(request.soDienThoai())
                .build();

        Patient savedPatient =
                patientRepository.save(patient);

        String token =
                jwtService.generateToken(savedPatient.getEmail());

        return new AuthResponse(
                token,
                savedPatient.getId(),
                savedPatient.getHoTen(),
                savedPatient.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {

        Patient patient =
                patientRepository
                        .findByEmail(request.email())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email hoặc mật khẩu không đúng"
                                ));

        if (!passwordEncoder.matches(
                request.matKhau(),
                patient.getMatKhau())) {

            throw new RuntimeException(
                    "Email hoặc mật khẩu không đúng"
            );
        }

        String token =
                jwtService.generateToken(patient.getEmail());

        return new AuthResponse(
                token,
                patient.getId(),
                patient.getHoTen(),
                patient.getEmail()
        );
    }
}
