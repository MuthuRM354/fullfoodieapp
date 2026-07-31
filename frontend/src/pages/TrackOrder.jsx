import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getTracking, getTrackingHistory } from '../api/tracking';
import { getOrder } from '../api/orders';

const STATUS_STEP = {
  PENDING:          0,
  CONFIRMED:        1,
  PREPARING:        2,
  READY_FOR_PICKUP: 3,
  OUT_FOR_DELIVERY: 4,
  DELIVERED:        5,
};

const STEPS = [
  { key: 'CONFIRMED',        icon: '✅', label: 'Order Confirmed' },
  { key: 'PREPARING',        icon: '👨‍🍳', label: 'Preparing' },
  { key: 'READY_FOR_PICKUP', icon: '📦', label: 'Ready for Pickup' },
  { key: 'OUT_FOR_DELIVERY', icon: '🛵', label: 'On the Way' },
  { key: 'DELIVERED',        icon: '🎉', label: 'Delivered' },
];

export default function TrackOrder() {
  const { orderId } = useParams();
  const [order, setOrder]       = useState(null);
  const [tracking, setTracking] = useState(null);
  const [history, setHistory]   = useState([]);
  const [loading, setLoading]   = useState(true);

  const fetchData = useCallback(async () => {
    try {
      const [oRes, tRes] = await Promise.allSettled([
        getOrder(orderId),
        getTracking(orderId),
      ]);
      if (oRes.status === 'fulfilled') {
        setOrder(oRes.value.data?.data || oRes.value.data);
      }
      if (tRes.status === 'fulfilled') {
        setTracking(tRes.value.data?.data || tRes.value.data);
      }
    } catch {
      toast.error('Failed to load tracking information');
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  const fetchHistory = useCallback(async () => {
    try {
      const res = await getTrackingHistory(orderId);
      const data = res.data?.data || res.data;
      setHistory(Array.isArray(data) ? data : []);
    } catch {
      // optional
    }
  }, [orderId]);

  useEffect(() => {
    fetchData();
    fetchHistory();
    // Poll every 15 seconds for live updates
    const t = setInterval(fetchData, 15000);
    return () => clearInterval(t);
  }, [fetchData, fetchHistory]);

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading tracking info…</span>
    </div>
  );

  const currentStep = order ? (STATUS_STEP[order.status] ?? -1) : -1;

  return (
    <div className="container page">
      <div style={{ marginBottom: 'var(--sp-4)' }}>
        <Link to="/orders" className="back-link">← Back to Orders</Link>
      </div>

      <h1 className="page-heading">Track Order #{orderId}</h1>
      {order?.restaurantName && (
        <p className="page-sub">from {order.restaurantName}</p>
      )}

      {/* Live location card */}
      {tracking && (
        <div className="track-location-card">
          <div className="track-location-card__inner">
            <div className="track-location-card__icon">🛵</div>
            <div>
              <div className="track-location-card__label">Delivery Partner Location</div>
              {tracking.latitude && tracking.longitude ? (
                <div className="track-location-card__coords">
                  {Number(tracking.latitude).toFixed(4)}°N, {Number(tracking.longitude).toFixed(4)}°E
                </div>
              ) : (
                <div className="track-location-card__coords">Location updating soon…</div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Progress steps */}
      <div className="track-steps-card">
        <h2 className="track-steps-title">Order Progress</h2>
        <div className="track-steps">
          {STEPS.map((step, idx) => {
            const stepNum = STATUS_STEP[step.key];
            const done    = currentStep > stepNum;
            const active  = currentStep === stepNum;
            return (
              <div key={step.key} className={`track-step ${done ? 'track-step--done' : ''} ${active ? 'track-step--active' : ''}`}>
                <div className="track-step__dot">
                  {done ? '✓' : active ? step.icon : ''}
                </div>
                {idx < STEPS.length - 1 && <div className={`track-step__line ${done ? 'track-step__line--done' : ''}`} />}
                <div className="track-step__label">{step.label}</div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Order summary */}
      {order && (
        <div className="track-order-summary">
          <h2 className="track-steps-title">Order Summary</h2>
          {(order.items || []).map((item, i) => (
            <div key={i} className="order-item-row">
              <span>{item.name} × {item.quantity}</span>
              <span>₹{(item.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
          <hr className="order-card__sep" />
          <div className="order-card__total">
            <span>Total</span>
            <span>₹{order.totalAmount?.toFixed(2)}</span>
          </div>
          {order.deliveryAddress && (
            <p style={{ marginTop: 'var(--sp-3)', fontSize: 'var(--fs-sm)', color: 'var(--text-3)' }}>
              📍 {order.deliveryAddress}
            </p>
          )}
        </div>
      )}

      {/* Location history */}
      {history.length > 0 && (
        <div className="track-order-summary" style={{ marginTop: 'var(--sp-5)' }}>
          <h2 className="track-steps-title">Location History</h2>
          {history.slice(0, 5).map((loc, i) => (
            <div key={i} style={{ fontSize: 'var(--fs-sm)', color: 'var(--text-3)', padding: '4px 0' }}>
              {Number(loc.latitude).toFixed(4)}°N, {Number(loc.longitude).toFixed(4)}°E
              {loc.recordedAt && ` — ${new Date(loc.recordedAt).toLocaleTimeString('en-IN')}`}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
