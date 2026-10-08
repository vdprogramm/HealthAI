package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.MedicalRecordRequest;
import com.vinh.healthcare.entity.Appointment;
import com.vinh.healthcare.entity.MedicalRecord;
import com.vinh.healthcare.repository.AppointmentRepository;
import com.vinh.healthcare.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinh.healthcare.dto.MedicalRecordResponse;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public MedicalRecord createMedicalRecord(
            MedicalRecordRequest request
    ) {

        Appointment appointment =
                appointmentRepository
                        .findById(request.appointmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy lịch hẹn"
                                ));

        if (medicalRecordRepository
                .existsByAppointmentId(appointment.getId())) {

            throw new IllegalStateException(
                    "Lịch hẹn này đã có bệnh án"
            );
        }

        MedicalRecord record =
                MedicalRecord.builder()
                        .appointment(appointment)
                        .trieuChungNhapVao(
                                request.trieuChungNhapVao()
                        )
                        .chuyenKhoaGoiYBoiAi(
                                request.chuyenKhoaGoiYBoiAi()
                        )
                        .chanDoanCuaBacSi(
                                request.chanDoanCuaBacSi()
                        )
                        .donThuoc(request.donThuoc())
                        .build();

        return medicalRecordRepository.save(record);
    }

    public MedicalRecordResponse getResponseByAppointment(
            Long appointmentId
    ) {

        MedicalRecord record =
                medicalRecordRepository
                        .findByAppointmentId(appointmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy bệnh án"
                                ));

        return new MedicalRecordResponse(
                record.getId(),
                record.getAppointment().getId(),
                record.getTrieuChungNhapVao(),
                record.getChuyenKhoaGoiYBoiAi(),
                record.getChanDoanCuaBacSi(),
                record.getDonThuoc()
        );
    }

    @Transactional
    public MedicalRecordResponse updateDiagnosisResponse(
            Long id,
            com.vinh.healthcare.dto.DiagnosisRequest request
    ) {

        MedicalRecord record =
                medicalRecordRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy bệnh án"
                                ));

        record.setChanDoanCuaBacSi(request.chanDoan());
        record.setDonThuoc(request.donThuoc());

        MedicalRecord saved = medicalRecordRepository.save(record);

        return new MedicalRecordResponse(
                saved.getId(),
                saved.getAppointment().getId(),
                saved.getTrieuChungNhapVao(),
                saved.getChuyenKhoaGoiYBoiAi(),
                saved.getChanDoanCuaBacSi(),
                saved.getDonThuoc()
        );
    }
}
