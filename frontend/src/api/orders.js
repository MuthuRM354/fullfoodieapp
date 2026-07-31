import api from './axios';

// Cart
export const getCart          = (userId)             => api.get(`/api/cart/${userId}`);
export const addToCart        = (userId, item)        => api.post(`/api/cart/${userId}/items`, item);
export const updateCartItem   = (userId, itemId, qty) =>
  api.put(`/api/cart/${userId}/items/${itemId}`, { quantity: qty });
export const removeCartItem   = (userId, itemId)     => api.delete(`/api/cart/${userId}/items/${itemId}`);
export const clearCart        = (userId)             => api.delete(`/api/cart/${userId}`);

// Orders — customer
export const placeOrder       = (data)               => api.post('/api/orders', data);
export const getOrders        = (userId)             => api.get(`/api/orders/user/${userId}`);
export const getOrder         = (id)                 => api.get(`/api/orders/${id}`);
// Backend uses PUT /{orderId}/status — no dedicated /cancel endpoint
export const cancelOrder      = (id)                 => api.put(`/api/orders/${id}/status`, { status: 'CANCELLED' });

// Orders — owner / admin / delivery
export const getOrdersByRestaurant = (restaurantId)  => api.get(`/api/orders/restaurant/${restaurantId}`);
export const updateOrderStatus     = (id, status)    => api.put(`/api/orders/${id}/status`, { status });
