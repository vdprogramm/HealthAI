import api from './api';
import type { AuthResponse } from '../types';

export interface LoginRequest {
  email: string;
  matKhau: string;
}

export interface RegisterRequest {
  hoTen: string;
  email: string;
  matKhau: string;
  soDienThoai: string;
}

export const authApi = {
  login: async (data: LoginRequest) => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest) => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  }
};
