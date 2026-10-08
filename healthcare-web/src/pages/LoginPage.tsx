import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.api'

export default function LoginPage() {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (
    e: FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault()

    try {
      setLoading(true)
      setError('')

      const data = await authApi.login({
        email,
        matKhau,
      })

      localStorage.setItem(
        'token',
        data.token,
      )

      localStorage.setItem(
        'user',
        JSON.stringify(data),
      )

      navigate('/')
    } catch (err: any) {
      setError(
        err.response?.data?.message ??
          'Đăng nhập thất bại',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form
        className="auth-card"
        onSubmit={handleSubmit}
      >
        <div className="logo">HealthAI</div>

        <h1>Đăng nhập</h1>

        <p className="muted">
          Đăng nhập để quản lý lịch khám của bạn
        </p>

        {error && (
          <div className="error-box">{error}</div>
        )}

        <label>Email</label>

        <input
          type="email"
          placeholder="example@gmail.com"
          value={email}
          onChange={(e) =>
            setEmail(e.target.value)
          }
          required
        />

        <label>Mật khẩu</label>

        <input
          type="password"
          placeholder="••••••••"
          value={matKhau}
          onChange={(e) =>
            setMatKhau(e.target.value)
          }
          required
        />

        <button
          className="primary-button"
          disabled={loading}
        >
          {loading
            ? 'Đang đăng nhập...'
            : 'Đăng nhập'}
        </button>

        <p className="auth-footer">
          Chưa có tài khoản?{' '}
          <Link to="/register">
            Đăng ký
          </Link>
        </p>
      </form>
    </div>
  )
}
