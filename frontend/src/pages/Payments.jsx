import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import { getPaymentByOrder } from '../api/payments';
import { getOrders } from '../api/orders';
import { useAuth } from '../context/AuthContext';

const STATUS_BADGE = {
  SUCCESS:   'badge badge-green',
  PENDING:   'badge badge-orange',
  FAILED:    'badge badge-red',
  REFUNDED:  'badge badge-blue',
};

// Keys match PaymentMethod enum: CASH, UPI, CARD, WALLET
const METHOD_ICON = {
  CASH:   '💵',
  UPI:    '📱',
  CARD:   '💳',
  WALLET: '👛',
};

export default function Payments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading]   = useState(true);
  const { user } = useAuth();

  const fetchPayments = useCallback(async () => {
    try {
      const ordersRes = await getOrders(user.id);
      const ordersData = ordersRes.data?.data || ordersRes.data;
      const orders = Array.isArray(ordersData) ? ordersData : [];

      // Fetch payment info for each order that's not PENDING/CANCELLED
      const paidOrders = orders.filter(o => !['PENDING', 'CANCELLED'].includes(o.status));
      const results = await Promise.allSettled(
        paidOrders.map(o => getPaymentByOrder(o.id))
      );

      const paymentList = results
        .filter(r => r.status === 'fulfilled')
        .map(r => r.value.data?.data || r.value.data)
        .filter(Boolean);

      setPayments(paymentList.sort((a, b) => b.id - a.id));
    } catch {
      toast.error('Failed to load payment history');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => { fetchPayments(); }, [fetchPayments]);

  const formatDate = (d) => {
    if (!d) return '';
    return new Date(d).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' });
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading payment history…</span>
    </div>
  );

  return (
    <div className="container page">
      <h1 className="page-heading">Payment History</h1>
      <p className="page-sub">All your transactions in one place</p>

      {payments.length === 0 ? (
        <div className="empty-wrap">
          <div className="empty-icon">💳</div>
          <div className="empty-title">No payment records yet</div>
          <p className="empty-text">Your payment history will appear here after your first order.</p>
        </div>
      ) : (
        <div>
          {payments.map((p) => (
            <div key={p.id} className="payment-card">
              <div className="payment-card__left">
                <div className="payment-card__icon">
                  {METHOD_ICON[p.paymentMethod] || '💰'}
                </div>
                <div>
                  <div className="payment-card__id">Payment #{p.id}</div>
                  <div className="payment-card__meta">
                    Order #{p.orderId} · {p.paymentMethod || 'CASH'}
                  </div>
                  {p.createdAt && (
                    <div className="payment-card__date">{formatDate(p.createdAt)}</div>
                  )}
                  {p.transactionId && (
                    <div className="payment-card__txn">TXN: {p.transactionId}</div>
                  )}
                </div>
              </div>
              <div className="payment-card__right">
                <div className="payment-card__amount">₹{Number(p.amount || 0).toFixed(2)}</div>
                <span className={STATUS_BADGE[p.status] || 'badge badge-grey'}>
                  {p.status || 'SUCCESS'}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
