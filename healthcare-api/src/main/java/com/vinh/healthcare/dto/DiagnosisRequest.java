package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotBlank;

public record DiagnosisRequest(

        @NotBlank
        String chanDoan,

        String donThuoc
) {
}
