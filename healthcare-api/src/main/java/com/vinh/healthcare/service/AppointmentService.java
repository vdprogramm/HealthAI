package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.AiAppointmentRequest;
import com.vinh.healthcare.dto.AiAppointmentResponse;
import com.vinh.healthcare.dto.AppointmentRequest;
import com.vinh.healthcare.entity.Appointment;
import com.vinh.healthcare.entity.AppointmentStatus;
import com.vinh.healthcare.entity.MedicalRecord;
import com.vinh.healthcare.entity.Patient;
import com.vinh.healthcare.entity.PaymentStatus;
import com.vinh.healthcare.entity.Schedule;
import com.vinh.healthcare.repository.AppointmentRepository;
import com.vinh.healthcare.repository.MedicalRecordRepository;
import com.vinh.healthcare.repository.PatientRepository;
import com.vinh.healthcare.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.vinh.healthcare.dto.AppointmentResponse;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ScheduleRepository scheduleRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            ScheduleRepository scheduleRepository,
            MedicalRecordRepository medicalRecordRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.scheduleRepository = scheduleRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    public Appointment createAppointment(
            String patientEmail,
            AppointmentRequest request
    ) {

        Patient patient =
                patientRepository.findByEmail(patientEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy bệnh nhân"
                                )
                        );

        // LOCK ROW SCHEDULE
        Schedule schedule =
                scheduleRepository
                        .findByIdForUpdate(request.scheduleId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy lịch khám"
                                )
                        );

        boolean alreadyBooked =
                appointmentRepository
                        .existsByScheduleIdAndTrangThaiNot(
                                schedule.getId(),
                                AppointmentStatus.CANCELED
                        );

        if (alreadyBooked) {
            throw new IllegalStateException(
                    "Khung giờ này đã được đặt"
            );
        }

        Appointment appointment =
                Appointment.builder()
                        .patient(patient)
                        .doctor(schedule.getDoctor())
                        .schedule(schedule)
                        .trangThai(
                                AppointmentStatus.CONFIRMED
                        )
                        .feeAmount(schedule.getDoctor().getGiaKham())
                        .paymentStatus(PaymentStatus.UNPAID)
                        .build();

        return appointmentRepository.save(appointment);
    }

    public List<AppointmentResponse> getMyAppointmentResponses(
            String patientEmail
    ) {

        Patient patient =
                patientRepository.findByEmail(patientEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy bệnh nhân"
                                )
                        );

        return appointmentRepository
                .findByPatientId(patient.getId())
                .stream()
                .map(a -> new AppointmentResponse(
                        a.getId(),
                        a.getTrangThai().name(),

                        a.getDoctor().getId(),
                        a.getDoctor().getTenBacSi(),
                        a.getDoctor().getChuyenKhoa(),

                        a.getSchedule().getId(),
                        a.getSchedule().getNgayKham(),
                        a.getSchedule().getGioBatDau(),
                        a.getSchedule().getGioKetThuc(),
                        a.getFeeAmount() == null ? a.getDoctor().getGiaKham() : a.getFeeAmount(),
                        a.getPaymentStatus().name()
                ))
                .toList();
    }

    @Transactional
    public Appointment cancelAppointment(
            String patientEmail,
            Long appointmentId
    ) {

        Patient patient = patientRepository
                .findByEmail(patientEmail)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Không tìm thấy bệnh nhân"
                        )
                );

        Appointment appointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Không tìm thấy lịch hẹn"
                                )
                        );

        if (!appointment.getPatient()
                .getId()
                .equals(patient.getId())) {

            throw new IllegalStateException(
                    "Bạn không có quyền hủy lịch hẹn này"
            );
        }

        if (appointment.getTrangThai()
                == AppointmentStatus.CANCELED) {

            throw new IllegalStateException(
                    "Lịch hẹn đã được hủy trước đó"
            );
        }

        appointment.setTrangThai(
                AppointmentStatus.CANCELED
        );

        return appointmentRepository.save(
                appointment
        );
    }

    @Transactional
    public AppointmentResponse markPaid(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn"));
        if (a.getTrangThai() == AppointmentStatus.CANCELED) {
            throw new IllegalStateException("Không thể thu tiền lịch đã hủy");
        }
        a.setPaymentStatus(PaymentStatus.PAID);
        appointmentRepository.save(a);
        return toResponse(a);
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getTrangThai().name(),
                a.getDoctor().getId(), a.getDoctor().getTenBacSi(), a.getDoctor().getChuyenKhoa(),
                a.getSchedule().getId(), a.getSchedule().getNgayKham(),
                a.getSchedule().getGioBatDau(), a.getSchedule().getGioKetThuc(),
                a.getFeeAmount() == null ? a.getDoctor().getGiaKham() : a.getFeeAmount(),
                a.getPaymentStatus().name());
    }

    @Transactional
    public AiAppointmentResponse createAiAppointment(
            String patientEmail,
            AiAppointmentRequest request
    ) {

        Patient patient = patientRepository
                .findByEmail(patientEmail)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Không tìm thấy bệnh nhân"
                        )
                );

        Schedule schedule = scheduleRepository
                .findByIdForUpdate(request.scheduleId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Không tìm thấy lịch khám"
                        )
                );

        if (!schedule.getDoctor()
                .getChuyenKhoa()
                .equalsIgnoreCase(
                        request.chuyenKhoaAi()
                )) {

            throw new IllegalStateException(
                    "Lịch khám không thuộc chuyên khoa AI đã gợi ý"
            );
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsByScheduleIdAndTrangThaiNot(
                                schedule.getId(),
                                AppointmentStatus.CANCELED
                        );

        if (alreadyBooked) {
            throw new IllegalStateException(
                    "Khung giờ này đã được đặt"
            );
        }

        Appointment appointment =
                Appointment.builder()
                        .patient(patient)
                        .doctor(schedule.getDoctor())
                        .schedule(schedule)
                        .trangThai(
                                AppointmentStatus.CONFIRMED
                        )
                        .feeAmount(schedule.getDoctor().getGiaKham())
                        .paymentStatus(PaymentStatus.UNPAID)
                        .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        MedicalRecord medicalRecord =
                MedicalRecord.builder()
                        .appointment(savedAppointment)
                        .trieuChungNhapVao(
                                request.trieuChung()
                        )
                        .chuyenKhoaGoiYBoiAi(
                                request.chuyenKhoaAi()
                        )
                        .build();

        MedicalRecord savedRecord =
                medicalRecordRepository.save(
                        medicalRecord
                );

        return new AiAppointmentResponse(
                savedAppointment.getId(),
                savedAppointment
                        .getTrangThai()
                        .name(),

                savedRecord.getId(),

                schedule.getDoctor().getId(),
                schedule.getDoctor().getTenBacSi(),
                schedule.getDoctor().getChuyenKhoa(),

                schedule.getNgayKham(),
                schedule.getGioBatDau(),
                schedule.getGioKetThuc(),

                savedRecord.getTrieuChungNhapVao(),
                savedRecord.getChuyenKhoaGoiYBoiAi()
        );
    }
}
