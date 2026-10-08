package com.vinh.healthcare.controller;

import com.vinh.healthcare.dto.AiTriageRequest;
import com.vinh.healthcare.dto.AiTriageResponse;
import com.vinh.healthcare.service.AiTriageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTriageController {

    private final AiTriageService aiTriageService;

    public AiTriageController(
            AiTriageService aiTriageService
    ) {
        this.aiTriageService = aiTriageService;
    }

    @PostMapping("/triage")
    public AiTriageResponse triage(
            @Valid @RequestBody AiTriageRequest request
    ) {
        return aiTriageService.triage(request);
    }
}
