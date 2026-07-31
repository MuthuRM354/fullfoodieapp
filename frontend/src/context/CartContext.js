import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getCart, addToCart, updateCartItem, removeCartItem, clearCart } from '../api/orders';
import { useAuth } from './AuthContext';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchCart = useCallback(async () => {
    if (!user?.id) return;
    setLoading(true);
    try {
      const res = await getCart(user.id);
      setCart(res.data?.data || null);
    } catch {
      setCart(null);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const addItem = async (item) => {
    if (!user?.id) return { success: false, message: 'Please login first' };
    try {
      const res = await addToCart(user.id, item);
      setCart(res.data?.data);
      return { success: true };
    } catch (err) {
      return { success: false, message: err.response?.data?.message || 'Failed to add item' };
    }
  };

  const updateItem = async (itemId, quantity) => {
    if (!user?.id) return;
    try {
      const res = await updateCartItem(user.id, itemId, quantity);
      setCart(res.data?.data);
    } catch (err) {
      console.error('Update cart error:', err);
    }
  };

  const removeItem = async (itemId) => {
    if (!user?.id) return;
    try {
      const res = await removeCartItem(user.id, itemId);
      setCart(res.data?.data);
    } catch (err) {
      console.error('Remove item error:', err);
    }
  };

  const clear = async () => {
    if (!user?.id) return;
    try {
      await clearCart(user.id);
      setCart(null);
    } catch (err) {
      console.error('Clear cart error:', err);
    }
  };

  const itemCount = cart?.items?.reduce((sum, i) => sum + i.quantity, 0) || 0;
  const total = cart?.items?.reduce((sum, i) => sum + i.price * i.quantity, 0) || 0;

  return (
    <CartContext.Provider value={{ cart, loading, addItem, updateItem, removeItem, clear, fetchCart, itemCount, total }}>
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
