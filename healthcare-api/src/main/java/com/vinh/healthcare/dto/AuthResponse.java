package com.vinh.healthcare.dto;

public record AuthResponse(
        String token,
        Long patientId,
        String hoTen,
        String email
) {
}
