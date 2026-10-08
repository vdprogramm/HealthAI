package com.vinh.healthcare.repository;

import com.vinh.healthcare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByChuyenKhoaIgnoreCase(String chuyenKhoa);
}
