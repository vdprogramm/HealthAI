import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.api'
import { getRole, homeFor } from '../auth/roles'
export default function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    try {
      setLoading(true); setError('')
      const data = await authApi.login({ email, matKhau })
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data))
      const role = getRole()
      if (!role) throw new Error('Token đăng nhập không hợp lệ')
      navigate(homeFor(role), { replace: true })
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Đăng nhập thất bại'
      setError(message)
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    } finally { setLoading(false) }
  }
  return <div className="auth-page"><form className="auth-card" onSubmit={handleSubmit}>
    <div className="logo">HealthAI</div><h1>Đăng nhập</h1>
    <p className="muted">Đăng nhập bằng tài khoản bệnh nhân, bác sĩ hoặc quản trị viên.</p>
    {error && <div className="error-box" role="alert">{error}</div>}
    <label htmlFor="email">Email</label><input id="email" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
    <label htmlFor="password">Mật khẩu</label><input id="password" type="password" value={matKhau} onChange={e => setMatKhau(e.target.value)} required />
    <button className="primary-button" disabled={loading}>{loading ? 'Đang đăng nhập...' : 'Đăng nhập'}</button>
    <p className="auth-footer">Chưa có tài khoản bệnh nhân? <Link to="/register">Đăng ký</Link></p>
  </form></div>
}
