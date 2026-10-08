package com.vinh.healthcare.repository;

import com.vinh.healthcare.entity.Schedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository
        extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDoctorId(Long doctorId);

    List<Schedule> findByDoctorIdAndNgayKham(
            Long doctorId,
            LocalDate ngayKham
    );

    boolean existsByDoctorIdAndNgayKhamAndGioBatDau(
            Long doctorId,
            LocalDate ngayKham,
            LocalTime gioBatDau
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Schedule s
            WHERE s.id = :id
            """)
    Optional<Schedule> findByIdForUpdate(
            @Param("id") Long id
    );

    @Query("""
        SELECT s
        FROM Schedule s
        WHERE s.doctor.id = :doctorId
          AND s.ngayKham >= CURRENT_DATE
          AND NOT EXISTS (
              SELECT a.id
              FROM Appointment a
              WHERE a.schedule.id = s.id
                AND a.trangThai <> com.vinh.healthcare.entity.AppointmentStatus.CANCELED
          )
        ORDER BY s.ngayKham, s.gioBatDau
    """)
    List<Schedule> findAvailableSchedules(
            @Param("doctorId") Long doctorId
    );
}
