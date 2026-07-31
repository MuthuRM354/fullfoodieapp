import api from './axios';

// Public
export const getRestaurants     = (params) => api.get('/api/restaurants', { params });
export const getRestaurant      = (id)     => api.get(`/api/restaurants/${id}`);
export const searchRestaurants  = (query)  => api.get('/api/restaurants', { params: { search: query } });

// Menu (public read)
export const getMenu            = (restaurantId) => api.get(`/api/restaurants/${restaurantId}/menu`);

// Owner — restaurant management
export const getRestaurantsByOwner = (ownerId) => api.get(`/api/restaurants/owner/${ownerId}`);
export const createRestaurant   = (data)         => api.post('/api/restaurants', data);
export const updateRestaurant   = (id, data)     => api.put(`/api/restaurants/${id}`, data);
export const deleteRestaurant   = (id)            => api.delete(`/api/restaurants/${id}`);

// Owner — menu management
export const addMenuItem        = (restaurantId, item)          => api.post(`/api/restaurants/${restaurantId}/menu`, item);
export const updateMenuItem     = (restaurantId, itemId, data)  => api.put(`/api/restaurants/${restaurantId}/menu/${itemId}`, data);
export const deleteMenuItem     = (restaurantId, itemId)        => api.delete(`/api/restaurants/${restaurantId}/menu/${itemId}`);
