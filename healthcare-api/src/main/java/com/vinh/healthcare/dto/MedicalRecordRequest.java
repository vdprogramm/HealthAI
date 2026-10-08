package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotNull;

public record MedicalRecordRequest(

        @NotNull(message = "appointmentId không được để trống")
        Long appointmentId,

        String trieuChungNhapVao,

        String chuyenKhoaGoiYBoiAi,

        String chanDoanCuaBacSi,

        String donThuoc
) {
}
