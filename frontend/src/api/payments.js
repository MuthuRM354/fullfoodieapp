import api from './axios';

export const initiatePayment  = (data)     => api.post('/api/payments/initiate', data);
export const getPayment       = (id)       => api.get(`/api/payments/${id}`);
export const getPaymentByOrder = (orderId) => api.get(`/api/payments/order/${orderId}`);
export const confirmPayment   = (id)       => api.post(`/api/payments/${id}/confirm`);
