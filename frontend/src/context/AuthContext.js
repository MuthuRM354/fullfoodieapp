import React, { createContext, useContext, useState } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')); } catch { return null; }
  });
  const [loading, setLoading] = useState(false);

  // Backend returns: { success: true, data: { token, userId, email, name, role } }
  // We normalize to: { id, email, name, role } and store token separately
  const normalizeResponse = (res) => {
    const payload = res.data?.data || res.data;
    const token = payload.token;
    const userData = {
      id: payload.userId,
      email: payload.email,
      name: payload.name,
      role: payload.role,
    };
    return { token, userData };
  };

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await apiLogin({ email, password });
      const { token, userData } = normalizeResponse(res);
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return { success: true };
    } catch (err) {
      return { success: false, message: err.response?.data?.message || 'Login failed' };
    } finally {
      setLoading(false);
    }
  };

  const register = async (data) => {
    setLoading(true);
    try {
      const res = await apiRegister(data);
      const { token, userData } = normalizeResponse(res);
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return { success: true };
    } catch (err) {
      return { success: false, message: err.response?.data?.message || 'Registration failed' };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
