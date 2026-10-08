package com.vinh.healthcare.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiAppointmentResponse(

        Long appointmentId,

        String trangThai,

        Long medicalRecordId,

        Long doctorId,

        String tenBacSi,

        String chuyenKhoa,

        LocalDate ngayKham,

        LocalTime gioBatDau,

        LocalTime gioKetThuc,

        String trieuChung,

        String chuyenKhoaGoiYBoiAi
) {
}
