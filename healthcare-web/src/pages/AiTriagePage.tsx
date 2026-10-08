import { useState } from 'react'
import {
  AlertTriangle,
  Bot,
  CalendarPlus,
  Stethoscope,
} from 'lucide-react'

import api from '../api/api'

import type {
  TriageResponse,
} from '../types'

export default function AiTriagePage() {
  const [symptoms, setSymptoms] =
    useState('')

  const [result, setResult] =
    useState<TriageResponse | null>(null)

  const [loading, setLoading] =
    useState(false)

  const [message, setMessage] =
    useState('')

  const analyze = async () => {
    if (!symptoms.trim()) return

    try {
      setLoading(true)
      setMessage('')
      setResult(null)

      const response =
        await api.post<TriageResponse>(
          '/ai/triage',
          {
            trieuChung: symptoms,
          },
        )

      setResult(response.data)
    } catch (error: unknown) {
      console.error('AI Triage request failed:', error)

      setMessage(
        'Dịch vụ AI đang tạm thời không khả dụng. ' +
        'Vui lòng thử lại sau ít phút.'
      )
    } finally {
      setLoading(false)
    }
  }

  const book = async (
    scheduleId: number,
  ) => {
    if (!result) return

    try {
      await api.post(
        '/appointments/ai',
        {
          scheduleId,
          trieuChung: symptoms,
          chuyenKhoaAi:
            result.chuyenKhoa,
        },
      )

      setMessage(
        'Đặt lịch khám thành công!',
      )

      await analyze()
    } catch (error: any) {
      setMessage(
        error.response?.data?.message ??
        'Đặt lịch thất bại',
      )
    }
  }

  return (
    <div>
      <div className="page-header">
        <Bot size={36} />

        <div>
          <h1>AI Triage</h1>

          <p>
            Mô tả triệu chứng để hệ thống
            hỗ trợ lựa chọn chuyên khoa.
          </p>
        </div>
      </div>

      <div className="triage-card">
        <label>
          Triệu chứng của bạn
        </label>

        <textarea
          rows={6}
          placeholder="Ví dụ: Tôi bị đau ngực trái và khó thở khi leo cầu thang..."
          value={symptoms}
          onChange={(e) =>
            setSymptoms(e.target.value)
          }
        />

        <button
          className="primary-button"
          onClick={analyze}
          disabled={loading}
        >
          {loading
            ? 'AI đang phân tích...'
            : 'Phân tích triệu chứng'}
        </button>
      </div>

      {message && (
        <div className="message-box">
          {message}
        </div>
      )}

      {result && (
        <>
          <div className="result-card">
            <div>
              <span className="result-label">
                Chuyên khoa gợi ý
              </span>

              <h2>
                <Stethoscope size={24} />
                {result.chuyenKhoa}
              </h2>
            </div>

            <div>
              <span className="result-label">
                Mức độ
              </span>

              <strong className="urgency">
                {result.mucDoKhanCap}
              </strong>
            </div>

            <p>
              {result.giaiThichNgan}
            </p>

            <div className="warning-box">
              <AlertTriangle size={20} />

              {result.canhBao}
            </div>
          </div>

          <h2 className="section-title">
            Bác sĩ & lịch khám phù hợp
          </h2>

          {result.doctors.length === 0 && (
            <div className="empty-card">
              Hiện chưa có bác sĩ thuộc
              chuyên khoa{' '}
              {result.chuyenKhoa}.
            </div>
          )}

          <div className="doctor-grid">
            {result.doctors.map(
              (doctor) => (
                <div
                  className="doctor-card"
                  key={doctor.doctorId}
                >
                  <div className="doctor-avatar">
                    {doctor.tenBacSi
                      .charAt(0)
                      .toUpperCase()}
                  </div>

                  <h3>
                    BS. {doctor.tenBacSi}
                  </h3>

                  <p>
                    {doctor.chuyenKhoa}
                  </p>

                  <strong>
                    {doctor.giaKham.toLocaleString(
                      'vi-VN',
                    )}
                    đ
                  </strong>

                  <div className="schedule-list">
                    {doctor.schedules.map(
                      (schedule) => (
                        <button
                          key={
                            schedule.scheduleId
                          }
                          className="schedule-button"
                          onClick={() =>
                            book(
                              schedule.scheduleId,
                            )
                          }
                        >
                          <CalendarPlus
                            size={17}
                          />

                          {schedule.ngayKham}

                          {' • '}

                          {schedule.gioBatDau}
                        </button>
                      ),
                    )}

                    {doctor.schedules.length ===
                      0 && (
                        <span className="muted">
                          Chưa có lịch trống
                        </span>
                      )}
                  </div>
                </div>
              ),
            )}
          </div>
        </>
      )}
    </div>
  )
}
