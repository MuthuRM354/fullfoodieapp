import api from './axios';

export const getReviews = (restaurantId) => api.get(`/api/reviews/restaurant/${restaurantId}`);
export const getRating = (restaurantId) => api.get(`/api/reviews/restaurant/${restaurantId}/summary`);
export const addReview = (data) => api.post('/api/reviews', data);
export const getUserReviews = (userId) => api.get(`/api/reviews/user/${userId}`);
