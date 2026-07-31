import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getOrders, cancelOrder } from '../api/orders';
import { addReview } from '../api/reviews';
import { useAuth } from '../context/AuthContext';

const ORDER_STEPS = ['PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED'];

const STATUS_BADGE = {
  PENDING:          'badge badge-orange',
  CONFIRMED:        'badge badge-blue',
  PREPARING:        'badge badge-purple',
  READY_FOR_PICKUP: 'badge badge-blue',
  OUT_FOR_DELIVERY: 'badge badge-blue',
  DELIVERED:        'badge badge-green',
  CANCELLED:        'badge badge-red',
};

const STATUS_LABEL = {
  PENDING:          '⏳ Pending',
  CONFIRMED:        '✅ Confirmed',
  PREPARING:        '👨‍🍳 Preparing',
  READY_FOR_PICKUP: '📦 Ready for Pickup',
  OUT_FOR_DELIVERY: '🛵 On the way',
  DELIVERED:        '🎉 Delivered',
  CANCELLED:        '❌ Cancelled',
};

const ACTIVE_STATUSES = ['PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY'];

export default function Orders() {
  const [orders, setOrders]         = useState([]);
  const [loading, setLoading]       = useState(true);
  const [reviewModal, setReviewModal] = useState(null); // { orderId, restaurantId }
  const [reviewForm, setReviewForm] = useState({ rating: 0, comment: '' });
  const [hovered, setHovered]       = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  const fetchOrders = useCallback(async () => {
    try {
      const res  = await getOrders(user.id);
      const data = res.data?.data || res.data;
      setOrders(Array.isArray(data) ? data.sort((a, b) => b.id - a.id) : []);
    } catch {
      toast.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => {
    fetchOrders();
    const t = setInterval(fetchOrders, 30000);
    return () => clearInterval(t);
  }, [fetchOrders]);

  const handleCancel = async (orderId) => {
    try {
      await cancelOrder(orderId);
      toast.success('Order cancelled');
      fetchOrders();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Cannot cancel order at this stage');
    }
  };

  const openReviewModal = (order) => {
    setReviewModal({ orderId: order.id, restaurantId: order.restaurantId });
    setReviewForm({ rating: 0, comment: '' });
    setHovered(0);
  };

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (reviewForm.rating === 0) { toast.warn('Please select a star rating'); return; }
    setSubmitting(true);
    try {
      await addReview({
        restaurantId: reviewModal.restaurantId,
        userId: user.id,
        userName: user.name,
        rating: reviewForm.rating,
        comment: reviewForm.comment,
      });
      toast.success('Review submitted! Thank you.');
      setReviewModal(null);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit review');
    } finally {
      setSubmitting(false);
    }
  };

  const formatDate = (d) => {
    if (!d) return '';
    return new Date(d).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' });
  };

  const stepClass = (step, currentStatus) => {
    if (currentStatus === 'CANCELLED') return '';
    const stepIdx    = ORDER_STEPS.indexOf(step);
    const currentIdx = ORDER_STEPS.indexOf(currentStatus);
    if (stepIdx < currentIdx)  return 'done';
    if (stepIdx === currentIdx) return 'active';
    return '';
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading orders…</span>
    </div>
  );

  return (
    <div className="container page">
      <h1 className="page-heading">My Orders</h1>
      <p  className="page-sub">Track and manage all your orders</p>

      {orders.length === 0 && (
        <div className="empty-wrap">
          <div className="empty-icon">📦</div>
          <div className="empty-title">No orders yet</div>
          <p className="empty-text">You haven't placed any orders. Start ordering now!</p>
          <Link to="/" className="btn btn-primary" style={{ marginTop: 8 }}>
            Browse Restaurants
          </Link>
        </div>
      )}

      {orders.map((order) => {
        const canCancel   = ['PENDING', 'CONFIRMED'].includes(order.status);
        const isCancelled = order.status === 'CANCELLED';
        const isActive    = ACTIVE_STATUSES.includes(order.status);
        const isDelivered = order.status === 'DELIVERED';

        return (
          <div key={order.id} className="order-card">
            {/* Header */}
            <div className="order-card__head">
              <div>
                <div className="order-card__id">Order #{order.id}</div>
                {order.restaurantName && (
                  <div className="order-card__restaurant">{order.restaurantName}</div>
                )}
                <div className="order-card__date">{formatDate(order.createdAt)}</div>
                {order.deliveryAddress && (
                  <div style={{ fontSize: 'var(--fs-xs)', color: 'var(--text-3)', marginTop: 2 }}>
                    📍 {order.deliveryAddress}
                  </div>
                )}
              </div>
              <span className={STATUS_BADGE[order.status] || 'badge badge-grey'}>
                {STATUS_LABEL[order.status] || order.status}
              </span>
            </div>

            {/* Status track */}
            {!isCancelled && (
              <div style={{ padding: '0 var(--sp-5)' }}>
                <div className="status-track">
                  {ORDER_STEPS.map((step) => (
                    <div key={step} className={`status-step ${stepClass(step, order.status)}`}>
                      <div className="status-step__dot" />
                      <div className="status-step__label">
                        {step.replace('_', ' ').replace('OUT FOR', 'OUT\nFOR')}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Items */}
            <div className="order-card__body">
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
            </div>

            {/* Footer */}
            <div className="order-card__foot">
              {canCancel && (
                <button className="btn btn-danger btn-sm" onClick={() => handleCancel(order.id)}>
                  Cancel Order
                </button>
              )}
              {isActive && (
                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => navigate(`/track/${order.id}`)}
                >
                  🗺 Track Order
                </button>
              )}
              {isDelivered && (
                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => openReviewModal(order)}
                >
                  ⭐ Leave a Review
                </button>
              )}
            </div>
          </div>
        );
      })}

      {/* ── Review Modal ──────────────────────────────── */}
      {reviewModal && (
        <div className="modal-overlay" onClick={() => setReviewModal(null)}>
          <div className="modal-card" onClick={e => e.stopPropagation()}>
            <h2 className="modal-title">Leave a Review</h2>
            <form onSubmit={handleSubmitReview}>
              <div className="star-picker" style={{ marginBottom: 12 }}>
                {[1,2,3,4,5].map((s) => (
                  <span
                    key={s}
                    className={`star ${s <= (hovered || reviewForm.rating) ? 'star--filled' : ''}`}
                    onMouseEnter={() => setHovered(s)}
                    onMouseLeave={() => setHovered(0)}
                    onClick={() => setReviewForm(f => ({ ...f, rating: s }))}
                  >★</span>
                ))}
                {reviewForm.rating > 0 && (
                  <span className="star-label">{reviewForm.rating}/5</span>
                )}
              </div>
              <div className="form-group">
                <textarea
                  className="form-input"
                  rows={3}
                  placeholder="How was your experience? (optional)"
                  value={reviewForm.comment}
                  onChange={e => setReviewForm(f => ({ ...f, comment: e.target.value }))}
                  style={{ resize: 'vertical' }}
                />
              </div>
              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-ghost" style={{ color: 'var(--text-3)', background: '#eee' }}
                  onClick={() => setReviewModal(null)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Submitting…' : 'Submit'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
