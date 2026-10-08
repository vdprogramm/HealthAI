import api from './api';
import type { TriageResponse } from '../types';

export const triageApi = {
  analyze: async (trieuChung: string) => {
    const response = await api.post<TriageResponse>('/ai/triage', { trieuChung });
    return response.data;
  }
};
