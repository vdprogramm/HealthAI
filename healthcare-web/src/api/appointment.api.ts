import api from './api';
import type { Appointment } from '../types';

export const appointmentApi = {
  getMyAll: async () => {
    const response = await api.get<Appointment[]>('/appointments/me');
    return response.data;
  },
  bookAi: async (scheduleId: number, trieuChung: string, chuyenKhoaAi: string) => {
    const response = await api.post('/appointments/ai', {
      scheduleId,
      trieuChung,
      chuyenKhoaAi
    });
    return response.data;
  },
  cancel: async (appointmentId: number) => {
    const response = await api.patch(`/appointments/${appointmentId}/cancel`);
    return response.data;
  }
};
