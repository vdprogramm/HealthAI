package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiAppointmentRequest(

        @NotNull(message = "Schedule ID không được để trống")
        Long scheduleId,

        @NotBlank(message = "Triệu chứng không được để trống")
        String trieuChung,

        @NotBlank(message = "Chuyên khoa AI không được để trống")
        String chuyenKhoaAi
) {
}
