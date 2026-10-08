package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(

        @NotNull(message = "scheduleId không được để trống")
        Long scheduleId

) {
}
