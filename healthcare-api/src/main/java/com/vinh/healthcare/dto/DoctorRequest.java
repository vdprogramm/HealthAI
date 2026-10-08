package com.vinh.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DoctorRequest(

        @NotBlank(message = "Tên bác sĩ không được để trống")
        String tenBacSi,

        @NotBlank(message = "Chuyên khoa không được để trống")
        String chuyenKhoa,

        @NotNull(message = "Giá khám không được để trống")
        @Positive(message = "Giá khám phải lớn hơn 0")
        BigDecimal giaKham
) {
}
