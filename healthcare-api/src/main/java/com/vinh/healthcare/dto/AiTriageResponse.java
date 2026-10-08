package com.vinh.healthcare.dto;

import java.util.List;

public record AiTriageResponse(
        String chuyenKhoa,
        String mucDoKhanCap,
        String giaiThichNgan,
        String canhBao,
        List<DoctorScheduleResponse> doctors
) {
}
