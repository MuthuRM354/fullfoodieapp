import api from './axios';

export const getTracking      = (orderId)  => api.get(`/api/tracking/${orderId}`);
export const getTrackingHistory = (orderId) => api.get(`/api/tracking/${orderId}/history`);
export const updateLocation   = (data)     => api.post('/api/tracking/update', data);
