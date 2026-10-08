package com.vinh.healthcare.controller;

import com.vinh.healthcare.entity.Appointment;
import com.vinh.healthcare.entity.MedicalRecord;
import com.vinh.healthcare.repository.AppointmentRepository;
import com.vinh.healthcare.repository.DoctorRepository;
import com.vinh.healthcare.repository.MedicalRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctor/me")
public class DoctorWorkspaceController {
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;
    private final MedicalRecordRepository records;

    public DoctorWorkspaceController(DoctorRepository doctors, AppointmentRepository appointments,
                                     MedicalRecordRepository records) {
        this.doctors = doctors;
        this.appointments = appointments;
        this.records = records;
    }

    private Long doctorId(Authentication auth) {
        return doctors.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản bác sĩ"))
                .getId();
    }

    public record Booking(Long appointmentId, String trangThai, LocalDate ngayKham,
                          LocalTime gioBatDau, LocalTime gioKetThuc, BigDecimal phiKham,
                          String trangThaiThanhToan) {}

    public record RecordSummary(Long id, Long appointmentId, String trieuChungNhapVao,
                                String chuyenKhoaGoiYBoiAi, String chanDoanCuaBacSi,
                                String donThuoc) {}

    @GetMapping("/appointments")
    public List<Booking> myAppointments(Authentication auth) {
        return appointments.findByDoctorId(doctorId(auth)).stream()
                .map(a -> new Booking(a.getId(), a.getTrangThai().name(),
                        a.getSchedule().getNgayKham(), a.getSchedule().getGioBatDau(),
                        a.getSchedule().getGioKetThuc(),
                        a.getFeeAmount() == null ? a.getDoctor().getGiaKham() : a.getFeeAmount(),
                        a.getPaymentStatus() == null ? "UNPAID" : a.getPaymentStatus().name()))
                .toList();
    }

    @GetMapping("/medical-records")
    public List<RecordSummary> myMedicalRecords(Authentication auth) {
        return appointments.findByDoctorId(doctorId(auth)).stream()
                .map(Appointment::getId)
                .map(records::findByAppointmentId)
                .flatMap(java.util.Optional::stream)
                .map(r -> new RecordSummary(r.getId(), r.getAppointment().getId(),
                        r.getTrieuChungNhapVao(), r.getChuyenKhoaGoiYBoiAi(),
                        r.getChanDoanCuaBacSi(), r.getDonThuoc()))
                .toList();
    }
}
