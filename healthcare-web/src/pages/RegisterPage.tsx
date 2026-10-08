import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.api'
import AuthFeedback from '../components/AuthFeedback'
import axios from 'axios'

export default function RegisterPage() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    hoTen: '',
    email: '',
    matKhau: '',
    soDienThoai: '',
  })

  const [feedback,setFeedback] = useState<{kind:'success'|'error';title:string;message:string}|null>(null)
  const [slow,setSlow]=useState(false)
  const [loading, setLoading] = useState(false)
  useEffect(()=>{if(!loading)return;const t=window.setTimeout(()=>setSlow(true),8000);return()=>window.clearTimeout(t)},[loading])

  const handleSubmit = async (
    e: FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault()

    try {
      setLoading(true)
      setFeedback(null);setSlow(false)

      const data = await authApi.register(form)

      localStorage.setItem(
        'token',
        data.token,
      )

      localStorage.setItem(
        'user',
        JSON.stringify(data),
      )

      setFeedback({kind:'success',title:'Đăng ký thành công!',message:'Tài khoản đã được tạo. Chào mừng bạn đến HealthAI!'})
    } catch (err: unknown) {
      const serverMessage=axios.isAxiosError(err)?err.response?.data?.message:null
      setFeedback({kind:'error',title:'Đăng ký thất bại',message:typeof serverMessage==='string'?serverMessage:'Không thể tạo tài khoản. Vui lòng thử lại.'})
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
      {loading && <AuthFeedback kind="loading" title="Đang đăng ký" message={slow?'Máy chủ đang khởi động, vui lòng chờ...':'Đang tạo tài khoản...'}/>}
      {feedback && <AuthFeedback kind={feedback.kind} title={feedback.title} message={feedback.message} onContinue={()=>feedback.kind==='success'?navigate('/',{replace:true}):setFeedback(null)}/>}
    </div>
  )
}
