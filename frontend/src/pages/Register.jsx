import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const [form, setForm] = useState({
    name: '', email: '', password: '', confirmPassword: '', phone: '',
  });
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name || !form.email || !form.password) {
      toast.error('Please fill in all required fields'); return;
    }
    if (form.password !== form.confirmPassword) {
      toast.error('Passwords do not match'); return;
    }
    if (form.password.length < 6) {
      toast.error('Password must be at least 6 characters'); return;
    }
    setLoading(true);
    const result = await register({
      name: form.name, email: form.email,
      password: form.password, phone: form.phone,
    });
    setLoading(false);
    if (result.success) {
      toast.success('Account created! Welcome to FoodieApp 🎉');
      navigate('/');
    } else {
      toast.error(result.message || 'Registration failed');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ maxWidth: 480 }}>
        <div className="auth-logo">🍽️</div>
        <h1 className="auth-title">Create account</h1>
        <p  className="auth-sub">Join FoodieApp and start ordering</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name *</label>
            <input className="form-input" type="text" name="name"
              placeholder="John Doe" value={form.name} onChange={handleChange} required autoFocus />
          </div>

          <div className="form-group">
            <label className="form-label">Email address *</label>
            <input className="form-input" type="email" name="email"
              placeholder="you@example.com" value={form.email} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label className="form-label">Phone number</label>
            <input className="form-input" type="tel" name="phone"
              placeholder="+91 9876543210" value={form.phone} onChange={handleChange} />
          </div>

          <div className="form-row">
            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">Password *</label>
              <input className="form-input" type="password" name="password"
                placeholder="Min 6 chars" value={form.password} onChange={handleChange} required />
            </div>
            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">Confirm Password *</label>
              <input className="form-input" type="password" name="confirmPassword"
                placeholder="Repeat password" value={form.confirmPassword} onChange={handleChange} required />
            </div>
          </div>

          <button
            className="btn btn-primary btn-full btn-lg"
            type="submit"
            disabled={loading}
            style={{ marginTop: 20 }}
          >
            {loading ? 'Creating account…' : 'Create Account →'}
          </button>
        </form>

        <div className="auth-foot">
          Already have an account?&nbsp;<Link to="/login">Sign in</Link>
        </div>
      </div>
    </div>
  );
}
