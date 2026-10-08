import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { appointmentApi } from '../api/appointment.api'
import type { Appointment } from '../types'

export default function AppointmentDetailPage() {
  const { id } = useParams()

  const [appointment, setAppointment] =
    useState<Appointment | null>(null)

  useEffect(() => {
    appointmentApi.getMyAll()
      .then(data => {
        const found = data.find(
          item => item.appointmentId === Number(id),
        )

        setAppointment(found ?? null)
      })
  }, [id])

  if (!appointment) {
    return <p>Không tìm thấy lịch khám.</p>
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Chi tiết lịch khám</h1>
          <p>Appointment #{appointment.appointmentId}</p>
        </div>
      </div>

      <div className="record-card">
        <div>
          <span>Trạng thái</span>
          <p>{appointment.trangThai}</p>
        </div>

        <div>
          <span>Bác sĩ</span>
          <p>BS. {appointment.tenBacSi}</p>
        </div>

        <div>
          <span>Chuyên khoa</span>
          <p>{appointment.chuyenKhoa}</p>
        </div>

        <div>
          <span>Ngày khám</span>
          <p>{appointment.ngayKham}</p>
        </div>

        <div>
          <span>Thời gian</span>
          <p>
            {appointment.gioBatDau}
            {' - '}
            {appointment.gioKetThuc}
          </p>
        </div>

        <div><span>Phí khám</span><p>{Number(appointment.phiKham ?? 0).toLocaleString("vi-VN")}đ</p></div>
        <div><span>Thanh toán</span><p>{appointment.trangThaiThanhToan === "PAID" ? "Đã thanh toán" : "Thanh toán trực tiếp tại phòng khám"}</p></div>
        <Link
          className="primary-link"
          to={`/medical-record/${appointment.appointmentId}`}
        >
          Xem hồ sơ khám bệnh
        </Link>
      </div>
    </div>
  )
}
