package com.vinh.healthcare.controller;

import com.vinh.healthcare.dto.ScheduleRequest;
import com.vinh.healthcare.entity.Schedule;
import com.vinh.healthcare.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule createSchedule(
            @Valid @RequestBody ScheduleRequest request) {

        return scheduleService.createSchedule(request);
    }

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public Schedule getScheduleById(
            @PathVariable Long id) {

        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Schedule> getSchedulesByDoctor(
            @PathVariable Long doctorId) {

        return scheduleService
                .getSchedulesByDoctor(doctorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(
            @PathVariable Long id) {

        scheduleService.deleteSchedule(id);
    }
}
