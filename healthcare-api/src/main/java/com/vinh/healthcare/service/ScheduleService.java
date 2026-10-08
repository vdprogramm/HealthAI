package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.ScheduleRequest;
import com.vinh.healthcare.entity.Doctor;
import com.vinh.healthcare.entity.Schedule;
import com.vinh.healthcare.repository.DoctorRepository;
import com.vinh.healthcare.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            DoctorRepository doctorRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    public Schedule createSchedule(ScheduleRequest request) {

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy bác sĩ"));

        if (request.ngayKham().isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "Không thể tạo lịch khám trong quá khứ"
            );
        }

        if (!request.gioKetThuc().isAfter(request.gioBatDau())) {
            throw new IllegalStateException(
                    "Giờ kết thúc phải sau giờ bắt đầu"
            );
        }

        boolean exists =
                scheduleRepository
                        .existsByDoctorIdAndNgayKhamAndGioBatDau(
                                request.doctorId(),
                                request.ngayKham(),
                                request.gioBatDau()
                        );

        if (exists) {
            throw new RuntimeException(
                    "Bác sĩ đã có lịch tại khung giờ này"
            );
        }

        Schedule schedule = Schedule.builder()
                .doctor(doctor)
                .ngayKham(request.ngayKham())
                .gioBatDau(request.gioBatDau())
                .gioKetThuc(request.gioKetThuc())
                .build();

        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy lịch"));
    }

    public List<Schedule> getSchedulesByDoctor(Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Không tìm thấy bác sĩ");
        }

        return scheduleRepository.findByDoctorId(doctorId);
    }

    public int createDemoSchedules() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        int created = 0;
        for (Doctor doctor : doctorRepository.findAll()) {
            for (int offset = 1; offset <= 7; offset++) {
                LocalDate date = today.plusDays(offset);
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
                for (int hour : new int[]{8, 9, 14, 15}) {
                    for (int minute : new int[]{0, 30}) {
                        LocalTime start = LocalTime.of(hour, minute);
                        if (scheduleRepository.existsByDoctorIdAndNgayKhamAndGioBatDau(
                                doctor.getId(), date, start)) continue;
                        scheduleRepository.save(Schedule.builder()
                                .doctor(doctor).ngayKham(date)
                                .gioBatDau(start).gioKetThuc(start.plusMinutes(30))
                                .build());
                        created++;
                    }
                }
            }
        }
        return created;
    }

    public void deleteSchedule(Long id) {

        Schedule schedule = getScheduleById(id);

        scheduleRepository.delete(schedule);
    }
}
