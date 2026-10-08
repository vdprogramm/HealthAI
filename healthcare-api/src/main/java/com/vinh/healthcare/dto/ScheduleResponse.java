package com.vinh.healthcare.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleResponse(
        Long scheduleId,
        LocalDate ngayKham,
        LocalTime gioBatDau,
        LocalTime gioKetThuc
) {
}
