import {
  Bot,
  CalendarDays,
  ShieldCheck,
} from 'lucide-react'

import { Link } from 'react-router-dom'

export default function DashboardPage() {
  const raw =
    localStorage.getItem('user')

  const user =
    raw ? JSON.parse(raw) : null

  return (
    <div>
      <section className="hero">
        <div>
          <span className="badge">
            AI Healthcare Assistant
          </span>

          <h1>
            Xin chào,{' '}
            {user?.hoTen ?? 'bạn'}
          </h1>

          <p>
            Mô tả triệu chứng để AI hỗ trợ
            lựa chọn chuyên khoa và lịch khám
            phù hợp.
          </p>

          <Link
            className="primary-link"
            to="/ai"
          >
            Bắt đầu AI Triage
          </Link>
        </div>

        <div className="hero-icon">
          <Bot size={100} />
        </div>
      </section>

      <div className="feature-grid">
        <div className="feature-card">
          <Bot size={32} />

          <h3>AI Triage</h3>

          <p>
            Hỗ trợ điều hướng chuyên khoa
            dựa trên triệu chứng.
          </p>
        </div>

        <div className="feature-card">
          <CalendarDays size={32} />

          <h3>Đặt lịch trực tuyến</h3>

          <p>
            Xem bác sĩ và lịch khám còn
            khả dụng.
          </p>
        </div>

        <div className="feature-card">
          <ShieldCheck size={32} />

          <h3>An toàn</h3>

          <p>
            AI chỉ hỗ trợ phân loại, không
            thay thế chẩn đoán của bác sĩ.
          </p>
        </div>
      </div>
    </div>
  )
}
