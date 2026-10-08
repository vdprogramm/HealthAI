import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/api'
import type { AuthResponse } from '../types'

export default function RegisterPage() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    hoTen: '',
    email: '',
    matKhau: '',
    soDienThoai: '',
  })

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (
    e: FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault()

    try {
      setLoading(true)
      setError('')

      const response = await api.post<AuthResponse>(
        '/auth/register',
        form,
      )

      localStorage.setItem(
        'token',
        response.data.token,
      )

      localStorage.setItem(
        'user',
        JSON.stringify(response.data),
      )

      navigate('/')
    } catch (err: any) {
      setError(
        err.response?.data?.message ??
          'Đăng ký thất bại',
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

        <h1>Tạo tài khoản</h1>

        {error && (
          <div className="error-box">{error}</div>
        )}

        <label>Họ tên</label>

        <input
          value={form.hoTen}
          onChange={(e) =>
            setForm({
              ...form,
              hoTen: e.target.value,
            })
          }
          required
        />

        <label>Email</label>

        <input
          type="email"
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value,
            })
          }
          required
        />

        <label>Số điện thoại</label>

        <input
          value={form.soDienThoai}
          onChange={(e) =>
            setForm({
              ...form,
              soDienThoai: e.target.value,
            })
          }
        />

        <label>Mật khẩu</label>

        <input
          type="password"
          value={form.matKhau}
          onChange={(e) =>
            setForm({
              ...form,
              matKhau: e.target.value,
            })
          }
          required
        />

        <button
          className="primary-button"
          disabled={loading}
        >
          {loading
            ? 'Đang tạo...'
            : 'Đăng ký'}
        </button>

        <p className="auth-footer">
          Đã có tài khoản?{' '}
          <Link to="/login">
            Đăng nhập
          </Link>
        </p>
      </form>
    </div>
  )
}
