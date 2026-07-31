import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import { getNotifications, markRead, markAllRead, deleteNotification } from '../api/notifications';
import { useAuth } from '../context/AuthContext';

// Keys match NotificationType enum: ORDER, ORDER_UPDATE, PAYMENT_SUCCESS, PAYMENT_FAILED, PROMOTIONAL, ACCOUNT
const TYPE_ICON = {
  ORDER:           '📦',
  ORDER_UPDATE:    '📦',
  PAYMENT_SUCCESS: '💳',
  PAYMENT_FAILED:  '❌',
  PROMOTIONAL:     '🎁',
  ACCOUNT:         '👤',
};

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  const fetchNotifications = useCallback(async () => {
    try {
      const res = await getNotifications(user.id);
      const data = res.data?.data || res.data;
      setNotifications(Array.isArray(data) ? data.sort((a, b) => b.id - a.id) : []);
    } catch {
      toast.error('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => { fetchNotifications(); }, [fetchNotifications]);

  const handleMarkRead = async (id) => {
    try {
      await markRead(id);
      setNotifications(ns => ns.map(n => n.id === id ? { ...n, read: true } : n));
    } catch { toast.error('Failed to mark as read'); }
  };

  const handleMarkAllRead = async () => {
    try {
      await markAllRead(user.id);
      setNotifications(ns => ns.map(n => ({ ...n, read: true })));
      toast.success('All notifications marked as read');
    } catch { toast.error('Failed to mark all as read'); }
  };

  const handleDelete = async (id) => {
    try {
      await deleteNotification(id);
      setNotifications(ns => ns.filter(n => n.id !== id));
    } catch { toast.error('Failed to delete notification'); }
  };

  const unreadCount = notifications.filter(n => !n.read).length;

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading notifications…</span>
    </div>
  );

  return (
    <div className="container page">
      <div className="notif-page-head">
        <div>
          <h1 className="page-heading">Notifications</h1>
          <p className="page-sub">
            {unreadCount > 0 ? `${unreadCount} unread notification${unreadCount > 1 ? 's' : ''}` : 'You\'re all caught up!'}
          </p>
        </div>
        {unreadCount > 0 && (
          <button className="btn btn-outline btn-sm" onClick={handleMarkAllRead}>
            Mark all as read
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <div className="empty-wrap">
          <div className="empty-icon">🔔</div>
          <div className="empty-title">No notifications yet</div>
          <p className="empty-text">We'll let you know when something important happens.</p>
        </div>
      ) : (
        <div className="notif-list">
          {notifications.map((n) => (
            <div key={n.id} className={`notif-item ${n.read ? 'notif-item--read' : 'notif-item--unread'}`}>
              <div className="notif-item__icon">
                {TYPE_ICON[n.type] || '🔔'}
              </div>
              <div className="notif-item__body">
                <div className="notif-item__title">{n.title}</div>
                <div className="notif-item__msg">{n.message}</div>
                {n.createdAt && (
                  <div className="notif-item__time">
                    {new Date(n.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                  </div>
                )}
              </div>
              <div className="notif-item__actions">
                {!n.read && (
                  <button className="notif-action-btn" title="Mark as read" onClick={() => handleMarkRead(n.id)}>
                    ✓
                  </button>
                )}
                <button className="notif-action-btn notif-action-btn--del" title="Delete" onClick={() => handleDelete(n.id)}>
                  ✕
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
