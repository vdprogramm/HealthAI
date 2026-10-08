package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.*;
import com.vinh.healthcare.entity.Admin;
import com.vinh.healthcare.entity.Patient;
import com.vinh.healthcare.entity.Doctor;
import com.vinh.healthcare.repository.AdminRepository;
import com.vinh.healthcare.repository.DoctorRepository;
import com.vinh.healthcare.repository.PatientRepository;
import com.vinh.healthcare.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            PatientRepository patientRepository,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.patientRepository = patientRepository;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (patientRepository.existsByEmail(request.email())
                || adminRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email đã được sử dụng");
        }

        Patient patient = Patient.builder()
                .hoTen(request.hoTen())
                .email(request.email())
                .matKhau(passwordEncoder.encode(request.matKhau()))
                .soDienThoai(request.soDienThoai())
                .build();

        Patient saved = patientRepository.save(patient);

        return new AuthResponse(
                jwtService.generateToken(saved.getEmail(), "PATIENT"),
                saved.getId(),
                saved.getHoTen(),
                saved.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {

        Admin admin = adminRepository.findByEmail(request.email())
                .orElse(null);

        if (admin != null) {
            if (!passwordEncoder.matches(
                    request.matKhau(), admin.getMatKhau())) {
                throw invalidCredentials();
            }

            return new AuthResponse(
                    jwtService.generateToken(admin.getEmail(), "ADMIN"),
                    admin.getId(),
                    admin.getHoTen(),
                    admin.getEmail()
            );
        }

        Doctor doctor = doctorRepository.findByEmail(request.email())
                .orElse(null);

        if (doctor != null) {
            if (!passwordEncoder.matches(
                    request.matKhau(), doctor.getMatKhau())) {
                throw invalidCredentials();
            }

            return new AuthResponse(
                    jwtService.generateToken(doctor.getEmail(), "DOCTOR"),
                    doctor.getId(),
                    doctor.getTenBacSi(),
                    doctor.getEmail()
            );
        }

        Patient patient = patientRepository.findByEmail(request.email())
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(
                request.matKhau(), patient.getMatKhau())) {
            throw invalidCredentials();
        }

        return new AuthResponse(
                jwtService.generateToken(patient.getEmail(), "PATIENT"),
                patient.getId(),
                patient.getHoTen(),
                patient.getEmail()
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Email hoặc mật khẩu không đúng"
        );
    }
}
