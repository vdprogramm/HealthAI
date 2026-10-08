import { useEffect, useState } from 'react'
import { Stethoscope } from 'lucide-react'
import api from '../api/api'

interface Doctor {
  id: number
  tenBacSi: string
  chuyenKhoa: string
  giaKham: number
}

export default function DoctorsPage() {
  const [doctors, setDoctors] = useState<Doctor[]>([])

  useEffect(() => {
    api.get<Doctor[]>('/doctors')
      .then(res => setDoctors(res.data))
  }, [])

  return (
    <div>
      <div className="page-header">
        <Stethoscope size={36} />
        <div>
          <h1>Danh sách bác sĩ</h1>
          <p>Tìm bác sĩ và chuyên khoa phù hợp.</p>
        </div>
      </div>

      <div className="doctor-grid">
        {doctors.map(doctor => (
          <div className="doctor-card" key={doctor.id}>
            <div className="doctor-avatar">
              {doctor.tenBacSi.charAt(0)}
            </div>

            <h3>BS. {doctor.tenBacSi}</h3>

            <p>{doctor.chuyenKhoa}</p>

            <strong>
              {doctor.giaKham.toLocaleString('vi-VN')}đ
            </strong>
          </div>
        ))}
      </div>
    </div>
  )
}
