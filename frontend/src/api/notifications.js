import api from './axios';

export const getNotifications = (userId)   => api.get(`/api/notifications/${userId}`);
export const markRead         = (id)        => api.put(`/api/notifications/${id}/read`);
export const markAllRead      = (userId)    => api.put(`/api/notifications/${userId}/read-all`);
export const deleteNotification = (id)     => api.delete(`/api/notifications/${id}`);
