package com.vinh.healthcare.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        return Map.of(
                "message", "Welcome to HealthAI Admin",
                "role", "ADMIN",
                "status", "SUCCESS"
        );
    }
}
