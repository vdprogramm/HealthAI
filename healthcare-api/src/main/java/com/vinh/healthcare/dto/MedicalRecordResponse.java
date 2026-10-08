package com.vinh.healthcare.dto;

public record MedicalRecordResponse(
        Long id,
        Long appointmentId,
        String trieuChung,
        String chuyenKhoaAi,
        String chanDoan,
        String donThuoc
) {
}
