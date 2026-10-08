export interface AuthResponse {
  token: string
  patientId: number
  hoTen: string
  email: string
}

export interface Schedule {
  scheduleId: number
  ngayKham: string
  gioBatDau: string
  gioKetThuc: string
}

export interface Doctor {
  doctorId: number
  tenBacSi: string
  chuyenKhoa: string
  giaKham: number
  schedules: Schedule[]
}

export interface TriageResponse {
  chuyenKhoa: string
  mucDoKhanCap: string
  giaiThichNgan: string
  canhBao: string
  doctors: Doctor[]
}

export interface Appointment {
  appointmentId: number
  trangThai: string

  doctorId: number
  tenBacSi: string
  chuyenKhoa: string

  scheduleId: number
  ngayKham: string
  gioBatDau: string
  gioKetThuc: string
}

export interface MedicalRecord {
  id: number
  appointmentId: number
  trieuChung: string
  chuyenKhoaAi: string
  chanDoan: string | null
  donThuoc: string | null
}
