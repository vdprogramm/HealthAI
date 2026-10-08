package com.vinh.healthcare.controller;

import com.vinh.healthcare.dto.AiAppointmentRequest;
import com.vinh.healthcare.dto.AiAppointmentResponse;
import com.vinh.healthcare.dto.AppointmentRequest;
import com.vinh.healthcare.entity.Appointment;
import com.vinh.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vinh.healthcare.dto.AppointmentResponse;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentRequest request
    ) {

        return appointmentService.createAppointment(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/me")
    public List<AppointmentResponse> getMyAppointments(
            Authentication authentication
    ) {

        return appointmentService
                .getMyAppointmentResponses(
                        authentication.getName()
                );
    }

    @PatchMapping("/{id}/cancel")
    public Appointment cancelAppointment(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return appointmentService.cancelAppointment(
                authentication.getName(),
                id
        );
    }

    @PostMapping("/ai")
    @ResponseStatus(HttpStatus.CREATED)
    public AiAppointmentResponse createAiAppointment(
            Authentication authentication,
            @Valid @RequestBody AiAppointmentRequest request
    ) {
        return appointmentService.createAiAppointment(
                authentication.getName(),
                request
        );
    }
}
