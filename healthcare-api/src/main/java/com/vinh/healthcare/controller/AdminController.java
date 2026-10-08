package com.vinh.healthcare.controller;

import com.vinh.healthcare.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final PatientRepository patients;

    public AdminController(PatientRepository patients) {
        this.patients = patients;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
                "message", "Welcome to HealthAI Admin",
                "role", "ADMIN",
                "status", "SUCCESS",
                "patientCount", patients.count()
        );
    }
}
