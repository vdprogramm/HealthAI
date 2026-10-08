import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { CalendarDays, ClipboardList, LogOut, Stethoscope } from 'lucide-react'
import { doctorApi, type Doctor } from '../api/doctor.api'
export default function DoctorDashboardPage() {
  const navigate = useNavigate()
  const [doctor, setDoctor] = useState<Doctor | null>(null)
  const [error, setError] = useState('')
  useEffect(() => {
    const raw = localStorage.getItem('user')
    let email = ''
    try { email = raw ? String(JSON.parse(raw).email ?? '') : '' } catch { /* invalid local data */ }
    doctorApi.getDoctors().then(list => {
      const found = list.find(d => 'email' in d && d.email === email)
      if (found) setDoctor(found)
    }).catch(() => setError('Không tải được thông tin bác sĩ.'))
  }, [])
  const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); navigate('/login') }
  return <div className="portal-shell">
    <aside className="portal-sidebar"><h2>HealthAI Doctor</h2><span><Stethoscope size={18}/> Không gian bác sĩ</span><button onClick={logout}><LogOut size={18}/> Đăng xuất</button></aside>
    <main className="portal-main"><div className="portal-heading"><div><p>CỔNG THÔNG TIN BÁC SĨ</p><h1>Xin chào, {doctor?.tenBacSi ?? 'Bác sĩ'}</h1><span>{doctor?.chuyenKhoa ?? 'Quản lý công việc khám chữa bệnh'}</span></div></div>
    <div className="portal-stats"><div><CalendarDays/><strong>—</strong><span>Lịch khám của tôi</span></div><div><ClipboardList/><strong>—</strong><span>Hồ sơ bệnh án</span></div></div>
    <section className="portal-panel"><h2>Lịch khám được phân công</h2><p>Chưa có API lấy lịch khám theo tài khoản bác sĩ. Không hiển thị lịch khám của bệnh nhân khác khi chưa xác minh quyền truy cập.</p>{error && <p role="alert">{error}</p>}</section>
    <section className="portal-panel"><h2>Hồ sơ bệnh án</h2><p>Chức năng này sẽ được mở khi backend hỗ trợ xác thực bác sĩ phụ trách từng lịch hẹn.</p></section></main>
  </div>
}
