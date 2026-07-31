import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { getNotifications } from '../../api/notifications';

export default function Header() {
  const { user, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (!user) { setUnreadCount(0); return; }
    const fetchUnread = async () => {
      try {
        const res = await getNotifications(user.id);
        const data = res.data?.data || res.data;
        const list = Array.isArray(data) ? data : [];
        setUnreadCount(list.filter(n => !n.read).length);
      } catch {
        // non-critical — fail silently
      }
    };
    fetchUnread();
    const t = setInterval(fetchUnread, 60000);
    return () => clearInterval(t);
  }, [user]);

  const handleLogout = () => { logout(); navigate('/'); };

  return (
    <header className="header">
      <div className="header__inner">
        <Link to="/" className="header__logo">
          <span>🍔</span> FoodieApp
        </Link>

        <nav className="header__nav">
          <Link to="/" className="header__link">Home</Link>

          {user ? (
            <>
              {/* Notification bell */}
              <Link to="/notifications" className="header__notif">
                🔔
                {unreadCount > 0 && (
                  <span className="header__notif-badge">
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </Link>

              <Link to="/cart" className="header__cart">
                🛒 Cart
                {itemCount > 0 && (
                  <span className="header__cart-pill">{itemCount}</span>
                )}
              </Link>
              <Link to="/orders"  className="header__link">Orders</Link>
              <Link to="/profile" className="header__link">Profile</Link>
              <Link to="/payments" className="header__link">Payments</Link>

              {/* Role-specific dashboard links */}
              {user.role === 'RESTAURANT_OWNER' && (
                <Link to="/owner" className="header__link header__link--role">My Restaurant</Link>
              )}
              {user.role === 'DELIVERY_PARTNER' && (
                <Link to="/delivery" className="header__link header__link--role">Deliveries</Link>
              )}
              {user.role === 'ADMIN' && (
                <Link to="/admin" className="header__link header__link--role">Admin</Link>
              )}

              <button className="header__logout" onClick={handleLogout}>
                Sign out
              </button>
            </>
          ) : (
            <>
              <Link to="/login"    className="header__link">Login</Link>
              <Link to="/register" className="btn btn-ghost btn-sm" style={{ marginLeft: 4 }}>
                Register
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
