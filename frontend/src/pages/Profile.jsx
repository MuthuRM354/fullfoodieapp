import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getProfile, updateProfile, changePassword } from '../api/auth';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [form, setForm]         = useState({ name: '', phone: '', address: '' });
  const [loading, setLoading]   = useState(false);
  const [fetching, setFetching] = useState(true);

  // Password change state
  const [pwForm, setPwForm]     = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [pwLoading, setPwLoading] = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        const res  = await getProfile();
        const data = res.data?.data || res.data;
        setForm({
          name:    data?.name    || user?.name    || '',
          phone:   data?.phone   || user?.phone   || '',
          address: data?.address || '',
        });
      } catch {
        setForm({ name: user?.name || '', phone: user?.phone || '', address: '' });
      } finally {
        setFetching(false);
      }
    };
    load();
  }, [user]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await updateProfile(form);
      toast.success('Profile updated successfully!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handlePwChange = (e) => setPwForm({ ...pwForm, [e.target.name]: e.target.value });

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      toast.error('New password and confirm password do not match');
      return;
    }
    if (pwForm.newPassword.length < 6) {
      toast.error('New password must be at least 6 characters');
      return;
    }
    setPwLoading(true);
    try {
      await changePassword(pwForm);
      toast.success('Password changed successfully!');
      setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to change password');
    } finally {
      setPwLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    toast.info('Signed out');
    navigate('/');
  };

  const initials = (form.name || user?.email || '?').charAt(0).toUpperCase();

  if (fetching) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading profile…</span>
    </div>
  );

  return (
    <div className="container page">
      <h1 className="page-heading">My Profile</h1>
      <p  className="page-sub">Manage your account details and preferences</p>

      <div className="profile-layout">
        {/* Sidebar */}
        <aside className="profile-sidebar">
          <div className="profile-avatar">{initials}</div>
          <div className="profile-name">{form.name || 'No name set'}</div>
          <div className="profile-email">{user?.email}</div>
          {user?.role && <div className="profile-role">{user.role}</div>}

          <hr className="profile-divider" />

          <button className="profile-action profile-action--danger" onClick={handleLogout}>
            🚪 Sign Out
          </button>
        </aside>

        {/* Main content */}
        <div className="profile-main">
          {/* Account Information */}
          <div className="profile-main__head">
            <span className="profile-main__title">Account Information</span>
          </div>
          <div className="profile-main__body">
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label className="form-label">Email address</label>
                <input
                  className="form-input readonly"
                  type="email"
                  value={user?.email || ''}
                  readOnly
                />
              </div>

              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input
                  className="form-input"
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Your full name"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <input
                  className="form-input"
                  type="tel"
                  name="phone"
                  value={form.phone}
                  onChange={handleChange}
                  placeholder="+91 9876543210"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Default Delivery Address</label>
                <input
                  className="form-input"
                  type="text"
                  name="address"
                  value={form.address}
                  onChange={handleChange}
                  placeholder="e.g. 42, MG Road, Bangalore 560001"
                />
              </div>

              <button
                className="btn btn-primary"
                type="submit"
                disabled={loading}
              >
                {loading ? 'Saving…' : 'Save Changes'}
              </button>
            </form>
          </div>

          {/* Change Password */}
          <div className="profile-main__head" style={{ marginTop: 'var(--sp-8)' }}>
            <span className="profile-main__title">Change Password</span>
          </div>
          <div className="profile-main__body">
            <form onSubmit={handleChangePassword}>
              <div className="form-group">
                <label className="form-label">Current Password</label>
                <input
                  className="form-input"
                  type="password"
                  name="currentPassword"
                  value={pwForm.currentPassword}
                  onChange={handlePwChange}
                  placeholder="Enter current password"
                  autoComplete="current-password"
                />
              </div>

              <div className="form-group">
                <label className="form-label">New Password</label>
                <input
                  className="form-input"
                  type="password"
                  name="newPassword"
                  value={pwForm.newPassword}
                  onChange={handlePwChange}
                  placeholder="At least 6 characters"
                  autoComplete="new-password"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Confirm New Password</label>
                <input
                  className="form-input"
                  type="password"
                  name="confirmPassword"
                  value={pwForm.confirmPassword}
                  onChange={handlePwChange}
                  placeholder="Repeat new password"
                  autoComplete="new-password"
                />
              </div>

              <button
                className="btn btn-outline"
                type="submit"
                disabled={pwLoading}
              >
                {pwLoading ? 'Updating…' : 'Update Password'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
