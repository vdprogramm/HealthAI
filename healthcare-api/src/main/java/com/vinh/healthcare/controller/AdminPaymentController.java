package com.vinh.healthcare.controller;

import com.vinh.healthcare.dto.AppointmentResponse;
import com.vinh.healthcare.service.AppointmentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
public class AdminPaymentController {
    private final AppointmentService service;
    public AdminPaymentController(AppointmentService service) { this.service = service; }

    @GetMapping
    public List<AppointmentResponse> list() { return service.getAllAppointmentsForAdmin(); }

    @PatchMapping("/{id}/mark-paid")
    public AppointmentResponse markPaid(@PathVariable Long id) {
        return service.markPaid(id);
    }
}
