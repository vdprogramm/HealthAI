package com.vinh.healthcare.repository;

import com.vinh.healthcare.entity.Appointment;
import com.vinh.healthcare.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByScheduleIdAndTrangThai(
            Long scheduleId,
            AppointmentStatus trangThai
    );

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);
}
