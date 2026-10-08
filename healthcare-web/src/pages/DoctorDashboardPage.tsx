import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { CalendarDays, ClipboardList, LogOut, Stethoscope } from 'lucide-react'
import { doctorApi, type Doctor } from '../api/doctor.api'
import api from '../api/api'

type Booking = {
  appointmentId: number
  trangThai: string
  ngayKham: string
  gioBatDau: string
  gioKetThuc: string
  phiKham: number
  trangThaiThanhToan: string
}
type MedicalRecord = {
  id: number
  appointmentId: number
  trieuChungNhapVao: string | null
  chuyenKhoaGoiYBoiAi: string | null
  chanDoanCuaBacSi: string | null
  donThuoc: string | null
}

export default function DoctorDashboardPage() {
  const navigate = useNavigate()
  const [doctor, setDoctor] = useState<Doctor | null>(null)
  const [appointments, setAppointments] = useState<Booking[]>([])
  const [records, setRecords] = useState<MedicalRecord[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    const raw = localStorage.getItem('user')
    let email = ''
    try { email = raw ? String(JSON.parse(raw).email ?? '') : '' } catch { /* invalid local data */ }

    Promise.all([
      doctorApi.getDoctors(),
      api.get<Booking[]>('/doctor/me/appointments'),
      api.get<MedicalRecord[]>('/doctor/me/medical-records'),
    ]).then(([doctors, bookings, medicalRecords]) => {
      if (!active) return
      setDoctor(doctors.find(d => 'email' in d && d.email === email) ?? null)
      setAppointments(bookings.data)
      setRecords(medicalRecords.data)
    }).catch(() => {
      if (active) setError('Không tải được dữ liệu bác sĩ. Vui lòng đăng nhập lại hoặc kiểm tra backend.')
    }).finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login')
  }

  return <div className="portal-shell">
    <aside className="portal-sidebar">
      <h2>HealthAI Doctor</h2>
      <span><Stethoscope size={18}/> Không gian bác sĩ</span>
      <button onClick={logout}><LogOut size={18}/> Đăng xuất</button>
    </aside>
    <main className="portal-main">
      <div className="portal-heading"><div>
        <p>CỔNG THÔNG TIN BÁC SĨ</p>
        <h1>Xin chào, {doctor?.tenBacSi ?? 'Bác sĩ'}</h1>
        <span>{doctor?.chuyenKhoa ?? 'Quản lý công việc khám chữa bệnh'}</span>
      </div></div>
      <div className="portal-stats">
        <div><CalendarDays/><strong>{loading ? '…' : appointments.length}</strong><span>Lịch khám của tôi</span></div>
        <div><ClipboardList/><strong>{loading ? '…' : records.length}</strong><span>Hồ sơ bệnh án</span></div>
      </div>
      {error && <section className="portal-panel"><p role="alert">{error}</p></section>}
      <section className="portal-panel">
        <h2>Lịch khám được phân công</h2>
        {loading ? <p>Đang tải lịch khám...</p> : appointments.length === 0 ? <p>Chưa có bệnh nhân đặt lịch với bác sĩ.</p> :
          <div className="portal-table-wrap"><table className="portal-table">
            <thead><tr><th>Mã lịch</th><th>Ngày</th><th>Giờ</th><th>Trạng thái</th><th>Phí khám</th><th>Thanh toán</th></tr></thead>
            <tbody>{appointments.map(a => <tr key={a.appointmentId}>
              <td>#{a.appointmentId}</td><td>{a.ngayKham}</td>
              <td>{a.gioBatDau} – {a.gioKetThuc}</td><td>{a.trangThai}</td>
              <td>{Number(a.phiKham).toLocaleString('vi-VN')}đ</td>
              <td>{a.trangThaiThanhToan === 'PAID' ? 'Đã thu' : 'Chưa thu'}</td>
            </tr>)}</tbody>
          </table></div>}
      </section>
      <section className="portal-panel">
        <h2>Hồ sơ bệnh án</h2>
        {loading ? <p>Đang tải bệnh án...</p> : records.length === 0 ? <p>Chưa có hồ sơ bệnh án cho các lịch hẹn của bác sĩ.</p> :
          <div className="portal-table-wrap"><table className="portal-table">
            <thead><tr><th>Mã hồ sơ</th><th>Mã lịch</th><th>Triệu chứng</th><th>Chuyên khoa AI</th><th>Chẩn đoán</th></tr></thead>
            <tbody>{records.map(r => <tr key={r.id}>
              <td>#{r.id}</td><td>#{r.appointmentId}</td><td>{r.trieuChungNhapVao ?? '—'}</td>
              <td>{r.chuyenKhoaGoiYBoiAi ?? '—'}</td><td>{r.chanDoanCuaBacSi ?? 'Chưa khám'}</td>
            </tr>)}</tbody>
          </table></div>}
      </section>
    </main>
  </div>
}
