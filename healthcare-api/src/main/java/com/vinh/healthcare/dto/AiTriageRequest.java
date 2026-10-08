package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotBlank;

public record AiTriageRequest(

        @NotBlank(message = "Triệu chứng không được để trống")
        String trieuChung

) {
}
