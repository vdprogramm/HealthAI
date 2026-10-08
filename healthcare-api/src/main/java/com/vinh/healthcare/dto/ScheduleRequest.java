package com.vinh.healthcare.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequest(

        @NotNull(message = "doctorId không được để trống")
        Long doctorId,

        @NotNull(message = "Ngày khám không được để trống")
        @FutureOrPresent(message = "Ngày khám không được ở quá khứ")
        LocalDate ngayKham,

        @NotNull(message = "Giờ bắt đầu không được để trống")
        LocalTime gioBatDau,

        @NotNull(message = "Giờ kết thúc không được để trống")
        LocalTime gioKetThuc
) {
}
