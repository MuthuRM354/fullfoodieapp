import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * Wraps protected routes — redirects to /login if not authenticated.
 */
export function PrivateRoute({ children }) {
  const { user } = useAuth();
  const location = useLocation();
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return children;
}

/**
 * Wraps public-only routes (login, register) — redirects to / if already authenticated.
 */
export function PublicRoute({ children }) {
  const { user } = useAuth();
  if (user) {
    return <Navigate to="/" replace />;
  }
  return children;
}

/**
 * Wraps role-specific routes.
 * Redirects to /login if not authenticated, or to / if the user's role is not in `roles`.
 * Usage: <RoleRoute roles={['ADMIN']}><AdminDashboard /></RoleRoute>
 */
export function RoleRoute({ children, roles }) {
  const { user } = useAuth();
  const location = useLocation();
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (!roles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }
  return children;
}
