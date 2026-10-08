package com.vinh.healthcare.dto;

import java.math.BigDecimal;
import java.util.List;

public record DoctorScheduleResponse(
        Long doctorId,
        String tenBacSi,
        String chuyenKhoa,
        BigDecimal giaKham,
        List<ScheduleResponse> schedules
) {
}
