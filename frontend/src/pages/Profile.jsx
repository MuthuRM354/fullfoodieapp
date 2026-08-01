import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getProfile, updateProfile, changePassword } from '../api/auth';
import { getAddresses, addAddress, updateAddress, deleteAddress } from '../api/addresses';
import { useAuth } from '../context/AuthContext';

const ADDRESS_LABELS = ['Home', 'Work', 'Other'];

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [form, setForm]         = useState({ name: '', phone: '', address: '' });
  const [loading, setLoading]   = useState(false);
  const [fetching, setFetching] = useState(true);

  // Password change state
  const [pwForm, setPwForm]     = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [pwLoading, setPwLoading] = useState(false);

  // Saved addresses state
  const [addresses, setAddresses]   = useState([]);
  const [addrForm, setAddrForm]     = useState({ label: 'Home', addressLine: '' });
  const [addrSaving, setAddrSaving] = useState(false);

  const loadAddresses = useCallback(async () => {
    if (!user?.id) return;
    try {
      const res = await getAddresses(user.id);
      const data = res.data?.data || res.data;
      setAddresses(Array.isArray(data) ? data : []);
    } catch {
      // non-critical — address book is optional
    }
  }, [user?.id]);

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
    loadAddresses();
  }, [user, loadAddresses]);

  const handleAddrFormChange = (e) => setAddrForm({ ...addrForm, [e.target.name]: e.target.value });

  const handleAddAddress = async (e) => {
    e.preventDefault();
    if (!addrForm.addressLine.trim()) { toast.error('Please enter the address'); return; }
    setAddrSaving(true);
    try {
      await addAddress(user.id, addrForm);
      toast.success('Address saved!');
      setAddrForm({ label: 'Home', addressLine: '' });
      loadAddresses();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save address');
    } finally {
      setAddrSaving(false);
    }
  };

  const handleSetDefault = async (addr) => {
    try {
      // Note: the backend's boolean field serializes as "default", not "isDefault"
      // (Lombok's isDefault() getter loses its "is" prefix under Jackson's bean
      // naming rules) — same convention already used for DeliveryPartner.isAvailable.
      await updateAddress(user.id, addr.id, { label: addr.label, addressLine: addr.addressLine, default: true });
      loadAddresses();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to set default address');
    }
  };

  const handleDeleteAddress = async (addressId) => {
    try {
      await deleteAddress(user.id, addressId);
      toast.info('Address removed');
      loadAddresses();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to remove address');
    }
  };

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

          {/* Saved Addresses */}
          <div className="profile-main__head" style={{ marginTop: 'var(--sp-8)' }}>
            <span className="profile-main__title">Saved Addresses</span>
          </div>
          <div className="profile-main__body">
            {addresses.length === 0 ? (
              <p className="empty-text" style={{ marginBottom: 'var(--sp-4)' }}>
                No saved addresses yet — add one below so you don't have to type it at checkout every time.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--sp-3)', marginBottom: 'var(--sp-5)' }}>
                {addresses.map((addr) => (
                  <div
                    key={addr.id}
                    style={{
                      display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
                      gap: 'var(--sp-3)', padding: 'var(--sp-3) var(--sp-4)',
                      border: `1.5px solid ${addr.default ? 'var(--primary)' : 'var(--border)'}`,
                      borderRadius: 'var(--r)',
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 700, fontSize: 'var(--fs-sm)' }}>
                        {addr.label} {addr.default && <span className="badge badge-green" style={{ marginLeft: 6 }}>Default</span>}
                      </div>
                      <div style={{ color: 'var(--text-muted)', fontSize: 'var(--fs-sm)', marginTop: 2 }}>
                        {addr.addressLine}
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: 6, flexShrink: 0 }}>
                      {!addr.default && (
                        <button className="btn btn-outline btn-sm" onClick={() => handleSetDefault(addr)}>
                          Set default
                        </button>
                      )}
                      <button className="btn btn-danger btn-sm" onClick={() => handleDeleteAddress(addr.id)}>
                        Remove
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}

            <form onSubmit={handleAddAddress}>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Label</label>
                  <select
                    className="form-input"
                    name="label"
                    value={addrForm.label}
                    onChange={handleAddrFormChange}
                  >
                    {ADDRESS_LABELS.map(l => <option key={l} value={l}>{l}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Address</label>
                  <input
                    className="form-input"
                    type="text"
                    name="addressLine"
                    value={addrForm.addressLine}
                    onChange={handleAddrFormChange}
                    placeholder="e.g. 42, MG Road, Bangalore 560001"
                  />
                </div>
              </div>
              <button className="btn btn-outline" type="submit" disabled={addrSaving}>
                {addrSaving ? 'Saving…' : '+ Add Address'}
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
