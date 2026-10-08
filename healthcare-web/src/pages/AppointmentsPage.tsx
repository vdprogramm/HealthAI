import {
  useEffect,
  useState,
} from 'react'

import {
  CalendarDays,
  XCircle,
} from 'lucide-react'
import { Link } from 'react-router-dom'

import { appointmentApi } from '../api/appointment.api'
import type { Appointment } from '../types'

export default function AppointmentsPage() {
  const [appointments, setAppointments] =
    useState<Appointment[]>([])

  const [loading, setLoading] =
    useState(true)

  const load = async () => {
    try {
      const data = await appointmentApi.getMyAll()
      setAppointments(data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const cancel = async (
    appointmentId: number,
  ) => {
    const confirmed =
      window.confirm(
        'Bạn có chắc muốn hủy lịch khám?',
      )

    if (!confirmed) return

    await appointmentApi.cancel(appointmentId)

    await load()
  }

  if (loading) {
    return <p>Đang tải...</p>
  }

  return (
    <div>
      <div className="page-header">
        <CalendarDays size={36} />

        <div>
          <h1>Lịch khám của tôi</h1>

          <p>
            Theo dõi các lịch khám đã đặt.
          </p>
        </div>
      </div>

      <div className="appointment-list">
        {appointments.map(
          (appointment) => (
            <div
              className="appointment-card"
              key={
                appointment.appointmentId
              }
            >
              <div>
                <span
                  className={`status ${appointment.trangThai.toLowerCase()}`}
                >
                  {appointment.trangThai}
                </span>

                <h3>
                  BS.{' '}
                  {appointment.tenBacSi}
                </h3>

                <p>
                  {appointment.chuyenKhoa}
                </p>
                <p><strong>Phí khám: {Number(appointment.phiKham ?? 0).toLocaleString("vi-VN")}đ</strong></p>
                <p>Thanh toán: {appointment.trangThaiThanhToan === "PAID" ? "Đã thanh toán tại phòng khám" : "Thanh toán trực tiếp tại phòng khám (chưa thu)"}</p>
              </div>

              <div>
                <strong>
                  {appointment.ngayKham}
                </strong>

                <p>
                  {appointment.gioBatDau}
                  {' - '}
                  {appointment.gioKetThuc}
                </p>
              </div>

              {appointment.trangThai ===
                'CONFIRMED' && (
                <button
                  className="danger-button"
                  onClick={() =>
                    cancel(
                      appointment.appointmentId,
                    )
                  }
                >
                  <XCircle size={17} />
                  Hủy lịch
                </button>
              )}

              <Link
                className="primary-link"
                to={`/appointments/${appointment.appointmentId}`}
              >
                Chi tiết
              </Link>
            </div>
          ),
        )}

        {appointments.length === 0 && (
          <div className="empty-card">
            Bạn chưa có lịch khám.
          </div>
        )}
      </div>
    </div>
  )
}
