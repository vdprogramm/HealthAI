import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { CalendarDays, Stethoscope, Users, LogOut, LayoutDashboard } from 'lucide-react'
import { doctorApi, type Doctor } from '../api/doctor.api'
export default function AdminDashboardPage() {
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [error, setError] = useState('')
  const navigate = useNavigate()
  useEffect(() => { doctorApi.getDoctors().then(setDoctors).catch(() => setError('Không tải được danh sách bác sĩ.')) }, [])
  const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); navigate('/login') }
  return <div className="portal-shell">
    <aside className="portal-sidebar"><h2>HealthAI Admin</h2><Link to="/admin/dashboard"><LayoutDashboard size={18}/> Tổng quan</Link><a href="#doctor-list"><Stethoscope size={18}/> Bác sĩ</a><button onClick={logout}><LogOut size={18}/> Đăng xuất</button></aside>
    <main className="portal-main"><div className="portal-heading"><div><p>TRANG QUẢN TRỊ</p><h1>Dashboard Admin</h1><span>Quản lý hoạt động của HealthAI</span></div></div>
    <div className="portal-stats"><div><Stethoscope/><strong>{doctors.length}</strong><span>Bác sĩ đang hiển thị</span></div><div><Users/><strong>—</strong><span>Bệnh nhân (chờ API)</span></div><div><CalendarDays/><strong>—</strong><span>Lịch khám (chờ API)</span></div></div>
    <section className="portal-panel" id="doctor-list"><h2>Danh sách bác sĩ</h2>{error && <p role="alert">{error}</p>}<div className="portal-table-wrap"><table className="portal-table"><thead><tr><th>Họ tên</th><th>Chuyên khoa</th><th>Giá khám</th></tr></thead><tbody>{doctors.map(d => <tr key={d.id}><td>{d.tenBacSi}</td><td>{d.chuyenKhoa}</td><td>{Number(d.giaKham).toLocaleString('vi-VN')} đ</td></tr>)}</tbody></table></div></section>
    <p className="portal-note">Các chức năng thêm/sửa/xóa bác sĩ, thống kê bệnh nhân và lịch khám cần API Admin được phân quyền trên backend.</p></main>
  </div>
}
