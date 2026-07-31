import api from './axios';

// User management (user-service via /api/admin/users)
export const getAllUsers      = ()         => api.get('/api/admin/users');
export const deactivateUser  = (id)       => api.put(`/api/admin/users/${id}/deactivate`);
export const activateUser    = (id)       => api.put(`/api/admin/users/${id}/activate`);

// Admin management (admin-service via /api/admin/admins)
export const getAdmins       = ()         => api.get('/api/admin/admins');
export const getAdmin        = (id)       => api.get(`/api/admin/admins/${id}`);
export const createAdmin     = (data)     => api.post('/api/admin/admins', data);
export const updateAdmin     = (id, data) => api.put(`/api/admin/admins/${id}`, data);
export const deactivateAdmin = (id)       => api.delete(`/api/admin/admins/${id}`);

// Audit logs
export const getAuditLogs    = ()         => api.get('/api/admin/audit');
