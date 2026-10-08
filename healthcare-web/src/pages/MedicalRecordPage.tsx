import {
  useEffect,
  useState,
} from 'react'

import {
  useParams,
} from 'react-router-dom'

import api from '../api/api'
import type { MedicalRecord } from '../types'

export default function MedicalRecordPage() {
  const { appointmentId } =
    useParams()

  const [record, setRecord] =
    useState<MedicalRecord | null>(null)

  const [error, setError] =
    useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const response =
          await api.get<MedicalRecord>(
            `/medical-records/appointment/${appointmentId}`,
          )

        setRecord(response.data)
      } catch {
        setError(
          'Không tìm thấy bệnh án.',
        )
      }
    }

    load()
  }, [appointmentId])

  if (error) {
    return (
      <div className="error-box">
        {error}
      </div>
    )
  }

  if (!record) {
    return <p>Đang tải bệnh án...</p>
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Hồ sơ khám bệnh</h1>

          <p>
            Appointment #
            {record.appointmentId}
          </p>
        </div>
      </div>

      <div className="record-card">
        <div>
          <span>Triệu chứng</span>
          <p>{record.trieuChung}</p>
        </div>

        <div>
          <span>
            Chuyên khoa AI gợi ý
          </span>

          <p>{record.chuyenKhoaAi}</p>
        </div>

        <div>
          <span>
            Chẩn đoán bác sĩ
          </span>

          <p>
            {record.chanDoan ??
              'Chưa có chẩn đoán'}
          </p>
        </div>

        <div>
          <span>Đơn thuốc</span>

          <p>
            {record.donThuoc ??
              'Chưa có đơn thuốc'}
          </p>
        </div>
      </div>
    </div>
  )
}
