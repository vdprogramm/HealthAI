import {
  CalendarDays,
  HeartPulse,
  LogOut,
  Sparkles,
  Stethoscope,
} from 'lucide-react'

import {
  Link,
  useNavigate,
} from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')

    navigate('/login')
  }

  return (
    <header className="navbar">
      <Link
        className="brand"
        to="/"
      >
        <HeartPulse size={28} />

        HealthAI
      </Link>

      <nav>
        <Link to="/ai">
          <Sparkles size={18} />
          AI Triage
        </Link>

        <Link to="/doctors">
          <Stethoscope size={18} />
          Bác sĩ
        </Link>

        <Link to="/appointments">
          <CalendarDays size={18} />
          Lịch khám
        </Link>

        <button
          className="logout-button"
          onClick={logout}
        >
          <LogOut size={18} />
          Đăng xuất
        </button>
      </nav>
    </header>
  )
}
