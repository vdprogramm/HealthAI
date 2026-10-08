package com.vinh.healthcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_bac_si", nullable = false)
    private String tenBacSi;

    @Column(name = "chuyen_khoa", nullable = false)
    private String chuyenKhoa;

    @Column(name = "gia_kham", nullable = false)
    private BigDecimal giaKham;

    @Column(unique = true)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "mat_khau")
    private String matKhau;
}
