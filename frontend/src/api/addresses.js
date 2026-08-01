import api from './axios';

export const getAddresses = (userId) => api.get(`/api/users/${userId}/addresses`);
export const addAddress = (userId, data) => api.post(`/api/users/${userId}/addresses`, data);
export const updateAddress = (userId, addressId, data) => api.put(`/api/users/${userId}/addresses/${addressId}`, data);
export const deleteAddress = (userId, addressId) => api.delete(`/api/users/${userId}/addresses/${addressId}`);
