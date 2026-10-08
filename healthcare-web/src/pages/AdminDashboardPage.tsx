import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { CalendarDays, Stethoscope, Users, LogOut, LayoutDashboard, Wallet } from 'lucide-react'
import { doctorApi, type Doctor } from '../api/doctor.api'
import api from '../api/api'

type Appointment = {
  appointmentId: number
  trangThai: string
  tenBacSi: string
  ngayKham: string
  gioBatDau: string
  gioKetThuc: string
  phiKham: number
  trangThaiThanhToan: 'UNPAID' | 'PAID'
}

export default function AdminDashboardPage() {
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [error, setError] = useState('')
  const [paymentMessage, setPaymentMessage] = useState('')
  const [loadingAppointments, setLoadingAppointments] = useState(true)
  const [payingId, setPayingId] = useState<number | null>(null)
  const navigate = useNavigate()

  const loadAppointments = async () => {
    try {
      const response = await api.get<Appointment[]>('/admin/appointments')
      setAppointments(response.data)
      setError('')
    } catch {
      setError('Không tải được lịch khám. Hãy kiểm tra tài khoản Admin và kết nối backend.')
    } finally {
      setLoadingAppointments(false)
    }
  }

  useEffect(() => {
    doctorApi.getDoctors().then(setDoctors).catch(() => setError('Không tải được danh sách bác sĩ.'))
    void loadAppointments()
  }, [])

  const markPaid = async (appointment: Appointment) => {
    if (appointment.trangThai === 'CANCELED' || appointment.trangThaiThanhToan === 'PAID') return
    if (!window.confirm(`Xác nhận đã nhận ${Number(appointment.phiKham).toLocaleString('vi-VN')}đ trực tiếp cho lịch #${appointment.appointmentId}?\nChỉ xác nhận khi đã thực sự thu tiền.`)) return
    setPayingId(appointment.appointmentId)
    setPaymentMessage('')
    try {
      await api.patch(`/admin/appointments/${appointment.appointmentId}/mark-paid`)
      await loadAppointments()
      setPaymentMessage(`Đã ghi nhận thanh toán cho lịch #${appointment.appointmentId}.`)
    } catch {
      setError('Không thể xác nhận thu tiền. Vui lòng kiểm tra quyền Admin và thử lại.')
    } finally {
      setPayingId(null)
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login')
  }

  const totalPaid = appointments.filter(a => a.trangThaiThanhToan === 'PAID')
    .reduce((sum, a) => sum + Number(a.phiKham ?? 0), 0)

  return <div className="portal-shell">
    <aside className="portal-sidebar">
      <h2>HealthAI Admin</h2>
      <Link to="/admin/dashboard"><LayoutDashboard size={18}/> Tổng quan</Link>
      <a href="#doctor-list"><Stethoscope size={18}/> Bác sĩ</a>
      <a href="#appointment-payments"><Wallet size={18}/> Thu phí khám</a>
      <button onClick={logout}><LogOut size={18}/> Đăng xuất</button>
    </aside>
    <main className="portal-main">
      <div className="portal-heading"><div><p>TRANG QUẢN TRỊ</p><h1>Dashboard Admin</h1><span>Quản lý hoạt động của HealthAI</span></div></div>
      <div className="portal-stats">
        <div><Stethoscope/><strong>{doctors.length}</strong><span>Bác sĩ đang hiển thị</span></div>
        <div><Users/><strong>—</strong><span>Bệnh nhân (chờ API)</span></div>
        <div><CalendarDays/><strong>{loadingAppointments ? '…' : appointments.length}</strong><span>Tổng lịch khám</span></div>
        <div><Wallet/><strong>{loadingAppointments ? '…' : totalPaid.toLocaleString('vi-VN') + 'đ'}</strong><span>Phí khám đã thu</span></div>
      </div>
      <section className="portal-panel" id="appointment-payments">
        <h2>Quản lý lịch khám và thu phí trực tiếp</h2>
        <p>Chỉ đánh dấu đã thu sau khi bệnh nhân thanh toán tại phòng khám.</p>
        {error && <p role="alert" style={{ color: '#b42318' }}>{error}</p>}
        {paymentMessage && <p role="status" style={{ color: '#087f5b' }}>{paymentMessage}</p>}
        {loadingAppointments ? <p>Đang tải lịch khám...</p> : appointments.length === 0 ? <p>Chưa có lịch khám nào.</p> :
          <div className="portal-table-wrap"><table className="portal-table">
            <thead><tr><th>Mã lịch</th><th>Bác sĩ</th><th>Ngày</th><th>Giờ</th><th>Phí khám</th><th>Lịch hẹn</th><th>Thanh toán</th><th>Thao tác</th></tr></thead>
            <tbody>{appointments.map(a => <tr key={a.appointmentId}>
              <td>#{a.appointmentId}</td><td>{a.tenBacSi}</td><td>{a.ngayKham}</td>
              <td>{a.gioBatDau}–{a.gioKetThuc}</td>
              <td>{Number(a.phiKham ?? 0).toLocaleString('vi-VN')}đ</td>
              <td>{a.trangThai}</td>
              <td>{a.trangThaiThanhToan === 'PAID' ? 'Đã thu' : 'Chưa thu'}</td>
              <td>{a.trangThai !== 'CANCELED' && a.trangThaiThanhToan !== 'PAID' ?
                <button type="button" className="portal-payment-button" disabled={payingId !== null} onClick={() => void markPaid(a)}>
                  {payingId === a.appointmentId ? 'Đang lưu...' : 'Xác nhận đã thu'}
                </button> : '—'}</td>
            </tr>)}</tbody>
          </table></div>}
      </section>
      <section className="portal-panel" id="doctor-list">
        <h2>Danh sách bác sĩ</h2>
        <div className="portal-table-wrap"><table className="portal-table">
          <thead><tr><th>Họ tên</th><th>Chuyên khoa</th><th>Giá khám</th></tr></thead>
          <tbody>{doctors.map(d => <tr key={d.id}><td>{d.tenBacSi}</td><td>{d.chuyenKhoa}</td><td>{Number(d.giaKham).toLocaleString('vi-VN')} đ</td></tr>)}</tbody>
        </table></div>
      </section>
      <p className="portal-note">Chức năng thống kê bệnh nhân và thêm/sửa/xóa bác sĩ sẽ được bổ sung sau.</p>
    </main>
  </div>
}
