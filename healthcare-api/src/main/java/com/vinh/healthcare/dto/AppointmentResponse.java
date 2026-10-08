package com.vinh.healthcare.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

public record AppointmentResponse(
        Long appointmentId,
        String trangThai,

        Long doctorId,
        String tenBacSi,
        String chuyenKhoa,

        Long scheduleId,
        LocalDate ngayKham,
        LocalTime gioBatDau,
        LocalTime gioKetThuc,
        BigDecimal phiKham,
        String trangThaiThanhToan
) {
}
