import api from './api';

export interface Doctor {
  id: number;
  tenBacSi: string;
  chuyenKhoa: string;
  giaKham: number;
}

export const doctorApi = {
  getDoctors: async () => {
    const response = await api.get<Doctor[]>('/doctors');
    return response.data;
  }
};
