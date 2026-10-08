package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.DoctorRequest;
import com.vinh.healthcare.entity.Doctor;
import com.vinh.healthcare.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy bác sĩ"));
    }

    public Doctor createDoctor(DoctorRequest request) {

        Doctor doctor = Doctor.builder()
                .tenBacSi(request.tenBacSi())
                .chuyenKhoa(request.chuyenKhoa())
                .giaKham(request.giaKham())
                .build();

        return doctorRepository.save(doctor);
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findByChuyenKhoaIgnoreCase(specialty);
    }

    public void deleteDoctor(Long id) {

        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bác sĩ");
        }

        doctorRepository.deleteById(id);
    }
}
