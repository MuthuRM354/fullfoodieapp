import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { getAllUsers, deactivateUser, activateUser, getAuditLogs } from '../api/admin';
import AdminHierarchy from '../components/admin/management/AdminHierarchy';

const TABS = ['Users', 'Audit Log', 'Hierarchy'];

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('Users');
  const [users, setUsers]         = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading]     = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const res = await getAllUsers();
        const data = res.data?.data || res.data;
        setUsers(Array.isArray(data) ? data : []);
      } catch {
        toast.error('Failed to load users');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  useEffect(() => {
    if (activeTab !== 'Audit Log') return;
    const load = async () => {
      try {
        const res = await getAuditLogs();
        const data = res.data?.data || res.data;
        setAuditLogs(Array.isArray(data) ? data : []);
      } catch {
        // audit logs may be empty — fail silently
      }
    };
    load();
  }, [activeTab]);

  const handleToggleUser = async (user) => {
    try {
      if (user.active !== false) {
        await deactivateUser(user.id);
        toast.success(`${user.name} deactivated`);
      } else {
        await activateUser(user.id);
        toast.success(`${user.name} activated`);
      }
      setUsers(us => us.map(u =>
        u.id === user.id ? { ...u, active: !u.active } : u
      ));
    } catch (err) {
      toast.error(err.response?.data?.message || 'Action failed');
    }
  };

  const filteredUsers = users.filter(u =>
    !searchTerm ||
    u.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const stats = {
    total:    users.length,
    active:   users.filter(u => u.active !== false).length,
    admins:   users.filter(u => u.role === 'ADMIN').length,
    owners:   users.filter(u => u.role === 'RESTAURANT_OWNER').length,
    delivery: users.filter(u => u.role === 'DELIVERY_PARTNER').length,
    customers:users.filter(u => u.role === 'CUSTOMER').length,
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading admin dashboard…</span>
    </div>
  );

  return (
    <div className="container page">
      <h1 className="page-heading">Admin Dashboard</h1>
      <p className="page-sub">Manage users, restaurants, and platform settings</p>

      {/* Stats row */}
      <div className="admin-stats-grid">
        {[
          { label: 'Total Users',  value: stats.total,     icon: '👥', color: 'blue' },
          { label: 'Active',       value: stats.active,    icon: '✅', color: 'green' },
          { label: 'Customers',    value: stats.customers, icon: '🧑', color: 'purple' },
          { label: 'Owners',       value: stats.owners,    icon: '🏪', color: 'orange' },
          { label: 'Delivery',     value: stats.delivery,  icon: '🛵', color: 'blue' },
          { label: 'Admins',       value: stats.admins,    icon: '🔑', color: 'red' },
        ].map(s => (
          <div key={s.label} className={`admin-stat-card admin-stat-card--${s.color}`}>
            <div className="admin-stat-card__icon">{s.icon}</div>
            <div className="admin-stat-card__value">{s.value}</div>
            <div className="admin-stat-card__label">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Tabs */}
      <div className="admin-tabs">
        {TABS.map(t => (
          <button
            key={t}
            className={`admin-tab ${activeTab === t ? 'admin-tab--active' : ''}`}
            onClick={() => setActiveTab(t)}
          >
            {t}
          </button>
        ))}
      </div>

      {/* Users tab */}
      {activeTab === 'Users' && (
        <>
          <div className="admin-search-row">
            <input
              className="form-input"
              placeholder="Search by name or email…"
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
              style={{ maxWidth: 360 }}
            />
          </div>
          <div className="admin-table-wrap">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(u => (
                  <tr key={u.id} className={u.active === false ? 'admin-table__row--inactive' : ''}>
                    <td>{u.id}</td>
                    <td>{u.name}</td>
                    <td>{u.email}</td>
                    <td>
                      <span className={`badge ${
                        u.role === 'ADMIN' ? 'badge-red' :
                        u.role === 'RESTAURANT_OWNER' ? 'badge-orange' :
                        u.role === 'DELIVERY_PARTNER' ? 'badge-blue' :
                        'badge-green'
                      }`}>
                        {u.role || 'CUSTOMER'}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${u.active !== false ? 'badge-green' : 'badge-red'}`}>
                        {u.active !== false ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      {u.role !== 'ADMIN' && (
                        <button
                          className={`btn btn-sm ${u.active !== false ? 'btn-danger' : 'btn-outline'}`}
                          onClick={() => handleToggleUser(u)}
                        >
                          {u.active !== false ? 'Deactivate' : 'Activate'}
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* Audit log tab */}
      {activeTab === 'Audit Log' && (
        <div className="admin-table-wrap">
          {auditLogs.length === 0 ? (
            <div className="empty-wrap" style={{ paddingTop: 'var(--sp-8)' }}>
              <div className="empty-icon">📋</div>
              <div className="empty-title">No audit logs available</div>
            </div>
          ) : (
            <table className="admin-table">
              <thead>
                <tr><th>ID</th><th>Action</th><th>Performed By</th><th>Target</th><th>Date</th></tr>
              </thead>
              <tbody>
                {auditLogs.map(log => (
                  <tr key={log.id}>
                    <td>{log.id}</td>
                    <td>{log.action}</td>
                    <td>{log.adminId}</td>
                    <td>{log.resourceId}</td>
                    <td>{log.createdAt ? new Date(log.createdAt).toLocaleString('en-IN') : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* Hierarchy tab */}
      {activeTab === 'Hierarchy' && (
        <div className="admin-table-wrap">
          <AdminHierarchy />
        </div>
      )}
    </div>
  );
}
