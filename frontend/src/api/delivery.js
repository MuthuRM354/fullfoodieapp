import api from './axios';

// Partner profile
// getPartner uses /user/{userId} because the frontend only knows the auth user ID, not the entity ID
export const getPartner       = (userId)   => api.get(`/api/delivery/partners/user/${userId}`);
export const updatePartner    = (id, data) => api.put(`/api/delivery/partners/${id}`, data);
export const setAvailability  = (id, avail) =>
  api.put(`/api/delivery/partners/${id}/availability`, { available: avail });

// Assignments
export const getAssignments   = (partnerId) =>
  api.get(`/api/delivery/assignments/partner/${partnerId}`);
export const updateAssignmentStatus = (id, status) =>
  api.put(`/api/delivery/assignments/${id}/status`, { status });

// Earnings
export const getEarnings      = (id)       => api.get(`/api/delivery/partners/${id}/earnings`);
export const getEarningsSummary = (id)     => api.get(`/api/delivery/partners/${id}/earnings/summary`);
