package com.vinh.healthcare.controller;

import com.vinh.healthcare.dto.DiagnosisRequest;
import com.vinh.healthcare.dto.MedicalRecordRequest;
import com.vinh.healthcare.entity.MedicalRecord;
import com.vinh.healthcare.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.vinh.healthcare.dto.MedicalRecordResponse;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService
    ) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecord create(
            @Valid @RequestBody MedicalRecordRequest request
    ) {
        return medicalRecordService
                .createMedicalRecord(request);
    }

    @GetMapping("/appointment/{appointmentId}")
    public MedicalRecordResponse getByAppointment(
            @PathVariable Long appointmentId
    ) {
        return medicalRecordService
                .getResponseByAppointment(appointmentId);
    }

    @PutMapping("/{id}/diagnosis")
    public MedicalRecordResponse updateDiagnosis(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosisRequest request
    ) {
        return medicalRecordService.updateDiagnosisResponse(
                id,
                request
        );
    }
}
