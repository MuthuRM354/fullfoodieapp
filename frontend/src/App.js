import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { PrivateRoute, PublicRoute, RoleRoute } from './components/common/Navigation';
import Header from './components/common/Header';
import Footer from './components/common/Footer';

// Existing pages
import Home             from './pages/Home';
import Login            from './pages/Login';
import Register         from './pages/Register';
import RestaurantDetail from './pages/RestaurantDetail';
import Cart             from './pages/Cart';
import Orders           from './pages/Orders';
import Profile          from './pages/Profile';

// New pages
import Notifications    from './pages/Notifications';
import TrackOrder       from './pages/TrackOrder';
import Payments         from './pages/Payments';
import AdminDashboard   from './pages/AdminDashboard';
import OwnerDashboard   from './pages/OwnerDashboard';
import DeliveryDashboard from './pages/DeliveryDashboard';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Header />
          <main>
            <Routes>
              {/* ── Public routes ──────────────────────────── */}
              <Route path="/" element={<Home />} />
              <Route path="/restaurants/:id" element={<RestaurantDetail />} />

              {/* ── Auth routes (redirect home if logged in) ─ */}
              <Route path="/login" element={
                <PublicRoute><Login /></PublicRoute>
              } />
              <Route path="/register" element={
                <PublicRoute><Register /></PublicRoute>
              } />

              {/* ── Customer protected routes ───────────────── */}
              <Route path="/cart" element={
                <PrivateRoute><Cart /></PrivateRoute>
              } />
              <Route path="/orders" element={
                <PrivateRoute><Orders /></PrivateRoute>
              } />
              <Route path="/profile" element={
                <PrivateRoute><Profile /></PrivateRoute>
              } />
              <Route path="/notifications" element={
                <PrivateRoute><Notifications /></PrivateRoute>
              } />
              <Route path="/track/:orderId" element={
                <PrivateRoute><TrackOrder /></PrivateRoute>
              } />
              <Route path="/payments" element={
                <PrivateRoute><Payments /></PrivateRoute>
              } />

              {/* ── Role-specific routes ──────────────────────
                   ADMIN, RESTAURANT_OWNER, DELIVERY_PARTNER
                   each get their own dashboard.
                   Any non-matching role gets redirected to /.
              ─────────────────────────────────────────────── */}
              <Route path="/admin" element={
                <RoleRoute roles={['ADMIN']}>
                  <AdminDashboard />
                </RoleRoute>
              } />
              <Route path="/owner" element={
                <RoleRoute roles={['RESTAURANT_OWNER']}>
                  <OwnerDashboard />
                </RoleRoute>
              } />
              <Route path="/delivery" element={
                <RoleRoute roles={['DELIVERY_PARTNER']}>
                  <DeliveryDashboard />
                </RoleRoute>
              } />

              {/* ── Catch-all ────────────────────────────────── */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
          <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} />
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
