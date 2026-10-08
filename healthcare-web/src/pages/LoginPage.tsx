import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.api'
import { getRole, homeFor } from '../auth/roles'
import AuthFeedback from '../components/AuthFeedback'
import axios from 'axios'
export default function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [feedback,setFeedback] = useState<{kind:'success'|'error';title:string;message:string;path?:string}|null>(null)
  const [slow,setSlow]=useState(false)
  useEffect(()=>{if(!loading)return;const t=window.setTimeout(()=>setSlow(true),8000);return()=>window.clearTimeout(t)},[loading])
  const [loading, setLoading] = useState(false)
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    try {
      setLoading(true); setFeedback(null); setSlow(false)
      const data = await authApi.login({ email, matKhau })
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data))
      const role = getRole()
      if (!role) throw new Error('Token đăng nhập không hợp lệ')
      setFeedback({kind:'success',title:'Đăng nhập thành công!',message:'Chào mừng bạn trở lại HealthAI.',path:homeFor(role)})
    } catch (err: unknown) {
      const serverMessage = axios.isAxiosError(err) ? err.response?.data?.message : null
      const message = axios.isAxiosError(err) && err.code === 'ECONNABORTED' ? 'Máy chủ phản hồi quá lâu. Vui lòng thử lại.' : typeof serverMessage === 'string' && /Email hoặc mật khẩu không đúng/.test(serverMessage) ? 'Email hoặc mật khẩu không đúng.' : typeof serverMessage === 'string' ? serverMessage : 'Không thể kết nối máy chủ.'
      setFeedback({kind:'error',title:'Đăng nhập thất bại',message})
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    } finally { setLoading(false) }
  }
  return <div className="auth-page"><form className="auth-card" onSubmit={handleSubmit}>
    <div className="logo">HealthAI</div><h1>Đăng nhập</h1>
    <p className="muted">Đăng nhập bằng tài khoản bệnh nhân, bác sĩ hoặc quản trị viên.</p>
    
    <label htmlFor="email">Email</label><input id="email" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
    <label htmlFor="password">Mật khẩu</label><input id="password" type="password" value={matKhau} onChange={e => setMatKhau(e.target.value)} required />
    <button className="primary-button" disabled={loading}>{loading ? 'Đang đăng nhập...' : 'Đăng nhập'}</button>
    <p className="auth-footer">Chưa có tài khoản bệnh nhân? <Link to="/register">Đăng ký</Link></p>
  </form>
  {loading && <AuthFeedback kind="loading" title="Đang đăng nhập" message={slow?"Máy chủ đang khởi động, vui lòng chờ...":"Đang xác thực tài khoản..."}/>}
  {feedback && <AuthFeedback kind={feedback.kind} title={feedback.title} message={feedback.message} onContinue={()=>feedback.path?navigate(feedback.path,{replace:true}):setFeedback(null)}/>}</div>
}
