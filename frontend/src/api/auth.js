import api from './axios';

export const register = (data) => api.post('/api/auth/register', data);
export const login = (data) => api.post('/api/auth/login', data);
// /api/users/me — resolves current user from the JWT token
export const getProfile = () => api.get('/api/users/me');
export const updateProfile = (data) => api.put('/api/users/me', data);
export const changePassword = (data) => api.post('/api/auth/change-password', data);
