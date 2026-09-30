import axios from 'axios';

const TOKEN_KEY = 'khata_token';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
    || (import.meta.env.DEV ? 'http://localhost:8080/api/v1' : '/api/v1'),
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  const isPublicAuthRequest = config.url?.startsWith('/auth/');
  if (token && !isPublicAuthRequest) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem('khata_user');
      if (window.location.hash !== '#/login?expired=true') {
        window.location.assign('/#/login?expired=true');
      }
    }
    return Promise.reject(error);
  },
);

export const authApi = {
  login: (payload) => api.post('/auth/login', payload),
  register: (payload) => api.post('/auth/register', payload),
  updatePreferences: (payload) => api.put('/auth/me/preferences', payload),
  updateProfile: (payload) => api.put('/auth/me', payload),
};

export const khataApi = {
  getTransactions: (userId) => api.get(`/khata/${userId}`),
  getTransactionsByPeriod: (userId, period) => api.get(`/khata/${userId}/range`, { params: { period } }),
  getSummary: (userId) => api.get(`/khata/${userId}/summary`),
  getAnalytics: (userId, period = 'month') => api.get(`/khata/${userId}/analytics`, { params: { period } }),
  createTransaction: (payload) => api.post('/khata', payload),
  deleteTransaction: (userId, transactionId) => api.delete(`/khata/${userId}/${transactionId}`),
  exportTransactions: (userId, format, period = 'month') => api.get(`/khata/${userId}/export/${format}`, { params: { period }, responseType: 'blob' }),
  getCategories: () => api.get('/categories'),
  createCategory: (payload) => api.post('/categories', payload),
  deleteCategory: (id) => api.delete(`/categories/${id}`),
};

export const adminApi = {
  getUsers: () => api.get('/admin/users'),
  createUser: (payload) => api.post('/admin/users', payload),
};

export { TOKEN_KEY };
