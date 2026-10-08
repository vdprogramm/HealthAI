import api from './api';
import type { MedicalRecord } from '../types';

export const medicalRecordApi = {
  getByAppointmentId: async (appointmentId: string | number) => {
    const response = await api.get<MedicalRecord>(`/medical-records/appointment/${appointmentId}`);
    return response.data;
  }
};
