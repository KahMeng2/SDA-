import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosError } from 'axios';

const axiosInstance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  withCredentials: true, // This is important for sending and receiving cookies
});

// Request interceptor
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // If refresh fails, log out the user or handle as needed
      localStorage.removeItem('authToken');
      // Optionally, redirect to login page or dispatch a logout action
      return Promise.reject(error);
    } else if (error.response?.status === 403) {
      // Handle 403 Forbidden errors
      console.error('Permission denied:', error.response.data);
      // You can dispatch a global action here to show a permission denied message
      // or handle it in a way that fits your application's structure
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
