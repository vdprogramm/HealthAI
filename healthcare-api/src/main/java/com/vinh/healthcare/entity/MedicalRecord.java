package com.vinh.healthcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "appointment_id",
        nullable = false,
        unique = true
    )
    private Appointment appointment;

    @Column(
        name = "trieu_chung_nhap_vao",
        columnDefinition = "TEXT"
    )
    private String trieuChungNhapVao;

    @Column(
        name = "chuyen_khoa_goi_y_boi_ai"
    )
    private String chuyenKhoaGoiYBoiAi;

    @Column(
        name = "chan_doan_cua_bac_si",
        columnDefinition = "TEXT"
    )
    private String chanDoanCuaBacSi;

    @Column(
        name = "don_thuoc",
        columnDefinition = "TEXT"
    )
    private String donThuoc;
}
