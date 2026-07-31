import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import {
  getPartner,
  getAssignments,
  updateAssignmentStatus,
  setAvailability,
  getEarningsSummary,
} from '../api/delivery';
import { useAuth } from '../context/AuthContext';

const TABS = ['Assignments', 'Earnings'];

const ASSIGNMENT_STATUS_LABEL = {
  ASSIGNED:   '🔔 Assigned',
  ACCEPTED:   '✅ Accepted',
  PICKED_UP:  '📦 Picked Up',
  DELIVERED:  '🎉 Delivered',
  REJECTED:   '❌ Rejected',
};

const NEXT_STATUS = {
  ASSIGNED:  'ACCEPTED',
  ACCEPTED:  'PICKED_UP',
  PICKED_UP: 'DELIVERED',
};

export default function DeliveryDashboard() {
  const { user } = useAuth();
  const [activeTab, setActiveTab]     = useState('Assignments');
  const [partner, setPartner]         = useState(null);
  const [assignments, setAssignments] = useState([]);
  const [earnings, setEarnings]       = useState(null);
  const [loading, setLoading]         = useState(true);

  const fetchAll = useCallback(async () => {
    try {
      // Step 1: fetch partner by auth userId (new /user/{userId} endpoint)
      const pRes = await getPartner(user.id);
      const partnerData = pRes.data?.data || pRes.data;
      setPartner(partnerData);

      // Step 2: fetch assignments using partner entity ID (not userId)
      if (partnerData?.id) {
        try {
          const aRes = await getAssignments(partnerData.id);
          const data = aRes.data?.data || aRes.data;
          setAssignments(Array.isArray(data) ? data.sort((a, b) => b.id - a.id) : []);
        } catch { /* assignments are optional — don't break the whole page */ }
      }
    } catch {
      toast.error('Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  const fetchEarnings = useCallback(async () => {
    if (!partner?.id) return;
    try {
      const res = await getEarningsSummary(partner.id);
      setEarnings(res.data?.data || res.data);
    } catch { /* optional */ }
  }, [partner?.id]);

  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  useEffect(() => {
    if (activeTab === 'Earnings') fetchEarnings();
  }, [activeTab, fetchEarnings]);

  const handleToggleAvailability = async () => {
    if (!partner) return;
    try {
      const newAvail = !partner.available;
      await setAvailability(partner.id, newAvail);
      setPartner(p => ({ ...p, available: newAvail }));
      toast.success(`You are now ${newAvail ? 'available' : 'unavailable'} for deliveries`);
    } catch { toast.error('Failed to update availability'); }
  };

  const handleUpdateStatus = async (assignment, status) => {
    try {
      await updateAssignmentStatus(assignment.id, status);
      setAssignments(as => as.map(a => a.id === assignment.id ? { ...a, status } : a));
      toast.success(`Assignment #${assignment.id} → ${status}`);
    } catch { toast.error('Failed to update status'); }
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" /><span>Loading delivery dashboard…</span>
    </div>
  );

  const activeAssignments   = assignments.filter(a => !['DELIVERED','REJECTED'].includes(a.status));
  const completedAssignments = assignments.filter(a => a.status === 'DELIVERED');

  return (
    <div className="container page">
      <h1 className="page-heading">Delivery Partner Dashboard</h1>
      <p className="page-sub">Welcome back, {user.name}</p>

      {/* Availability toggle */}
      <div className="delivery-avail-card">
        <div>
          <div className="delivery-avail-card__label">Availability Status</div>
          <div className="delivery-avail-card__sub">
            {partner?.available ? 'You are available to accept orders' : 'You are currently offline'}
          </div>
        </div>
        <button
          className={`btn btn-sm ${partner?.available ? 'btn-danger' : 'btn-primary'}`}
          onClick={handleToggleAvailability}
        >
          {partner?.available ? 'Go Offline' : 'Go Online'}
        </button>
      </div>

      {/* Quick stats */}
      <div className="admin-stats-grid" style={{ gridTemplateColumns: 'repeat(3, 1fr)' }}>
        <div className="admin-stat-card admin-stat-card--blue">
          <div className="admin-stat-card__icon">📦</div>
          <div className="admin-stat-card__value">{activeAssignments.length}</div>
          <div className="admin-stat-card__label">Active</div>
        </div>
        <div className="admin-stat-card admin-stat-card--green">
          <div className="admin-stat-card__icon">✅</div>
          <div className="admin-stat-card__value">{completedAssignments.length}</div>
          <div className="admin-stat-card__label">Delivered</div>
        </div>
        <div className="admin-stat-card admin-stat-card--orange">
          <div className="admin-stat-card__icon">⭐</div>
          <div className="admin-stat-card__value">{partner?.rating ? Number(partner.rating).toFixed(1) : '—'}</div>
          <div className="admin-stat-card__label">Rating</div>
        </div>
      </div>

      {/* Tabs */}
      <div className="admin-tabs">
        {TABS.map(t => (
          <button key={t} className={`admin-tab ${activeTab === t ? 'admin-tab--active' : ''}`}
            onClick={() => setActiveTab(t)}>{t}</button>
        ))}
      </div>

      {/* Assignments tab */}
      {activeTab === 'Assignments' && (
        <>
          {assignments.length === 0 ? (
            <div className="empty-wrap">
              <div className="empty-icon">🛵</div>
              <div className="empty-title">No assignments yet</div>
              <p className="empty-text">Go online to start receiving delivery requests.</p>
            </div>
          ) : (
            assignments.map(a => (
              <div key={a.id} className="order-card">
                <div className="order-card__head">
                  <div>
                    <div className="order-card__id">Assignment #{a.id}</div>
                    <div style={{ fontSize: 'var(--fs-sm)', color: 'var(--text-3)' }}>
                      Order #{a.orderId}
                      {a.deliveryAddress && ` · 📍 ${a.deliveryAddress}`}
                    </div>
                  </div>
                  <span className="badge badge-blue">{ASSIGNMENT_STATUS_LABEL[a.status] || a.status}</span>
                </div>
                <div className="order-card__foot">
                  {NEXT_STATUS[a.status] && (
                    <button className="btn btn-primary btn-sm"
                      onClick={() => handleUpdateStatus(a, NEXT_STATUS[a.status])}>
                      Mark as {NEXT_STATUS[a.status].replace('_', ' ')}
                    </button>
                  )}
                  {a.status === 'ASSIGNED' && (
                    <button className="btn btn-danger btn-sm"
                      onClick={() => handleUpdateStatus(a, 'REJECTED')}>Reject</button>
                  )}
                </div>
              </div>
            ))
          )}
        </>
      )}

      {/* Earnings tab */}
      {activeTab === 'Earnings' && (
        <div className="delivery-earnings">
          {earnings ? (
            <div className="delivery-earnings-grid">
              <div className="delivery-earnings-card">
                <div className="delivery-earnings-card__label">Today's Earnings</div>
                <div className="delivery-earnings-card__value">₹{Number(earnings.todayEarnings || 0).toFixed(2)}</div>
              </div>
              <div className="delivery-earnings-card">
                <div className="delivery-earnings-card__label">Weekly Earnings</div>
                <div className="delivery-earnings-card__value">₹{Number(earnings.weeklyEarnings || 0).toFixed(2)}</div>
              </div>
              <div className="delivery-earnings-card">
                <div className="delivery-earnings-card__label">Total Earnings</div>
                <div className="delivery-earnings-card__value">₹{Number(earnings.totalEarnings || 0).toFixed(2)}</div>
              </div>
              <div className="delivery-earnings-card">
                <div className="delivery-earnings-card__label">Total Deliveries</div>
                <div className="delivery-earnings-card__value">{earnings.totalDeliveries || 0}</div>
              </div>
            </div>
          ) : (
            <div className="empty-wrap">
              <div className="empty-icon">💰</div>
              <div className="empty-title">No earnings data yet</div>
              <p className="empty-text">Complete your first delivery to see earnings here.</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
