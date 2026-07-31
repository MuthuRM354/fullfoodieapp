import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import {
  getRestaurantsByOwner,
  createRestaurant,
  updateRestaurant,
  getMenu,
  addMenuItem,
  updateMenuItem,
  deleteMenuItem,
} from '../api/restaurants';
import { getOrdersByRestaurant, updateOrderStatus } from '../api/orders';
import { useAuth } from '../context/AuthContext';

const TABS = ['My Restaurants', 'Incoming Orders'];

export default function OwnerDashboard() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('My Restaurants');
  const [restaurants, setRestaurants] = useState([]);
  const [orders, setOrders]           = useState([]);
  const [loading, setLoading]         = useState(true);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [menu, setMenu]               = useState([]);
  const [showRestaurantForm, setShowRestaurantForm] = useState(false);
  const [restaurantForm, setRestaurantForm] = useState({
    name: '', description: '', cuisine: '', address: '', city: '', phone: '', email: '',
  });
  const [showMenuForm, setShowMenuForm] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [menuForm, setMenuForm] = useState({
    name: '', description: '', price: '', category: '', isVeg: false,
  });

  const fetchRestaurants = useCallback(async () => {
    try {
      const res = await getRestaurantsByOwner(user.id);
      const data = res.data?.data || res.data;
      const list = Array.isArray(data) ? data : [];
      setRestaurants(list);
      setSelectedRestaurant(prev => prev || list[0] || null);
    } catch {
      toast.error('Failed to load your restaurants');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  const fetchMenu = useCallback(async (restaurantId) => {
    try {
      const res = await getMenu(restaurantId);
      const data = res.data?.data || res.data;
      setMenu(Array.isArray(data) ? data : []);
    } catch { setMenu([]); }
  }, []);

  const fetchOrders = useCallback(async () => {
    if (!selectedRestaurant) return;
    try {
      const res = await getOrdersByRestaurant(selectedRestaurant.id);
      const data = res.data?.data || res.data;
      setOrders(Array.isArray(data) ? data.sort((a,b) => b.id - a.id) : []);
    } catch { setOrders([]); }
  }, [selectedRestaurant]);

  useEffect(() => { fetchRestaurants(); }, [fetchRestaurants]);
  useEffect(() => {
    if (selectedRestaurant) {
      fetchMenu(selectedRestaurant.id);
      fetchOrders();
    }
  }, [selectedRestaurant, fetchMenu, fetchOrders]);

  const handleCreateRestaurant = async (e) => {
    e.preventDefault();
    try {
      await createRestaurant({ ...restaurantForm, ownerId: user.id });
      toast.success('Restaurant created!');
      setShowRestaurantForm(false);
      setRestaurantForm({ name: '', description: '', cuisine: '', address: '', city: '', phone: '', email: '' });
      fetchRestaurants();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create restaurant');
    }
  };

  const handleToggleOpen = async (r) => {
    try {
      await updateRestaurant(r.id, { isOpen: !r.isOpen });
      setRestaurants(rs => rs.map(x => x.id === r.id ? { ...x, isOpen: !x.isOpen } : x));
      if (selectedRestaurant?.id === r.id) setSelectedRestaurant(s => ({ ...s, isOpen: !s.isOpen }));
      toast.success(`Restaurant marked as ${r.isOpen ? 'Closed' : 'Open'}`);
    } catch { toast.error('Failed to update status'); }
  };

  const handleEditMenuItem = (item) => {
    setEditingItem(item);
    setMenuForm({
      name: item.name || '',
      description: item.description || '',
      price: String(item.price || ''),
      category: item.category || '',
      isVeg: item.isVeg || false,
    });
    setShowMenuForm(true);
  };

  const resetMenuForm = () => {
    setShowMenuForm(false);
    setEditingItem(null);
    setMenuForm({ name: '', description: '', price: '', category: '', isVeg: false });
  };

  const handleSaveMenuItem = async (e) => {
    e.preventDefault();
    if (!selectedRestaurant) return;
    try {
      if (editingItem) {
        await updateMenuItem(selectedRestaurant.id, editingItem.id, { ...menuForm, price: parseFloat(menuForm.price) });
        toast.success('Menu item updated!');
      } else {
        await addMenuItem(selectedRestaurant.id, { ...menuForm, price: parseFloat(menuForm.price) });
        toast.success('Menu item added!');
      }
      resetMenuForm();
      fetchMenu(selectedRestaurant.id);
    } catch (err) {
      toast.error(err.response?.data?.message || `Failed to ${editingItem ? 'update' : 'add'} menu item`);
    }
  };

  const handleDeleteMenuItem = async (itemId) => {
    if (!selectedRestaurant) return;
    try {
      await deleteMenuItem(selectedRestaurant.id, itemId);
      setMenu(m => m.filter(i => i.id !== itemId));
      toast.success('Item removed');
    } catch { toast.error('Failed to remove item'); }
  };

  const handleOrderStatus = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status);
      setOrders(os => os.map(o => o.id === orderId ? { ...o, status } : o));
      toast.success(`Order #${orderId} → ${status}`);
    } catch { toast.error('Failed to update order status'); }
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" /><span>Loading owner dashboard…</span>
    </div>
  );

  return (
    <div className="container page">
      <h1 className="page-heading">Restaurant Owner Dashboard</h1>
      <p className="page-sub">Manage your restaurants and incoming orders</p>

      {/* Tabs */}
      <div className="admin-tabs">
        {TABS.map(t => (
          <button key={t} className={`admin-tab ${activeTab === t ? 'admin-tab--active' : ''}`}
            onClick={() => setActiveTab(t)}>{t}</button>
        ))}
      </div>

      {/* My Restaurants tab */}
      {activeTab === 'My Restaurants' && (
        <>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--sp-4)' }}>
            <h2 style={{ fontSize: 'var(--fs-lg)', fontWeight: 700 }}>
              Your Restaurants ({restaurants.length})
            </h2>
            <button className="btn btn-primary btn-sm" onClick={() => setShowRestaurantForm(v => !v)}>
              {showRestaurantForm ? 'Cancel' : '+ Add Restaurant'}
            </button>
          </div>

          {/* Add restaurant form */}
          {showRestaurantForm && (
            <form className="owner-form-card" onSubmit={handleCreateRestaurant}>
              <h3 style={{ marginBottom: 'var(--sp-4)' }}>New Restaurant</h3>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Restaurant Name *</label>
                  <input className="form-input" required value={restaurantForm.name}
                    onChange={e => setRestaurantForm(f => ({ ...f, name: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Cuisine Type</label>
                  <input className="form-input" value={restaurantForm.cuisine}
                    onChange={e => setRestaurantForm(f => ({ ...f, cuisine: e.target.value }))} />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <input className="form-input" value={restaurantForm.description}
                  onChange={e => setRestaurantForm(f => ({ ...f, description: e.target.value }))} />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Address</label>
                  <input className="form-input" value={restaurantForm.address}
                    onChange={e => setRestaurantForm(f => ({ ...f, address: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">City</label>
                  <input className="form-input" value={restaurantForm.city}
                    onChange={e => setRestaurantForm(f => ({ ...f, city: e.target.value }))} />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Phone</label>
                  <input className="form-input" value={restaurantForm.phone}
                    onChange={e => setRestaurantForm(f => ({ ...f, phone: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Email</label>
                  <input className="form-input" type="email" value={restaurantForm.email}
                    onChange={e => setRestaurantForm(f => ({ ...f, email: e.target.value }))} />
                </div>
              </div>
              <button type="submit" className="btn btn-primary">Create Restaurant</button>
            </form>
          )}

          {/* Restaurant list */}
          {restaurants.length === 0 ? (
            <div className="empty-wrap">
              <div className="empty-icon">🏪</div>
              <div className="empty-title">No restaurants yet</div>
              <p className="empty-text">Add your first restaurant to get started.</p>
            </div>
          ) : (
            <div className="owner-restaurant-list">
              {restaurants.map(r => (
                <div key={r.id} className={`owner-restaurant-card ${selectedRestaurant?.id === r.id ? 'owner-restaurant-card--selected' : ''}`}
                  onClick={() => setSelectedRestaurant(r)}>
                  <div className="owner-restaurant-card__info">
                    <div className="owner-restaurant-card__name">{r.name}</div>
                    <div className="owner-restaurant-card__meta">{r.cuisine || r.cuisineType} · {r.city}</div>
                  </div>
                  <div style={{ display: 'flex', gap: 'var(--sp-2)', alignItems: 'center' }}>
                    <span className={`badge ${r.isOpen ? 'badge-green' : 'badge-red'}`}>
                      {r.isOpen ? '🟢 Open' : '🔴 Closed'}
                    </span>
                    <button className="btn btn-outline btn-sm" onClick={e => { e.stopPropagation(); handleToggleOpen(r); }}>
                      {r.isOpen ? 'Close' : 'Open'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Menu management for selected restaurant */}
          {selectedRestaurant && (
            <div style={{ marginTop: 'var(--sp-8)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--sp-4)' }}>
                <h2 style={{ fontSize: 'var(--fs-lg)', fontWeight: 700 }}>
                  Menu — {selectedRestaurant.name}
                </h2>
                <button className="btn btn-primary btn-sm" onClick={() => showMenuForm ? resetMenuForm() : setShowMenuForm(true)}>
                  {showMenuForm ? 'Cancel' : '+ Add Item'}
                </button>
              </div>

              {showMenuForm && (
                <form className="owner-form-card" onSubmit={handleSaveMenuItem}>
                  <h3 style={{ marginBottom: 'var(--sp-4)' }}>{editingItem ? 'Edit Menu Item' : 'New Menu Item'}</h3>
                  <div className="form-row">
                    <div className="form-group">
                      <label className="form-label">Item Name *</label>
                      <input className="form-input" required value={menuForm.name}
                        onChange={e => setMenuForm(f => ({ ...f, name: e.target.value }))} />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Price (₹) *</label>
                      <input className="form-input" type="number" min="0" required value={menuForm.price}
                        onChange={e => setMenuForm(f => ({ ...f, price: e.target.value }))} />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label className="form-label">Category</label>
                      <input className="form-input" value={menuForm.category}
                        onChange={e => setMenuForm(f => ({ ...f, category: e.target.value }))} />
                    </div>
                    <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: 8, paddingTop: 28 }}>
                      <input type="checkbox" id="isVeg" checked={menuForm.isVeg}
                        onChange={e => setMenuForm(f => ({ ...f, isVeg: e.target.checked }))} />
                      <label htmlFor="isVeg" className="form-label" style={{ marginBottom: 0 }}>🌿 Vegetarian</label>
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Description</label>
                    <input className="form-input" value={menuForm.description}
                      onChange={e => setMenuForm(f => ({ ...f, description: e.target.value }))} />
                  </div>
                  <button type="submit" className="btn btn-primary">{editingItem ? 'Save Changes' : 'Add Item'}</button>
                </form>
              )}

              <div className="menu-grid">
                {menu.map(item => (
                  <div key={item.id} className="menu-item">
                    <div className="menu-item__top">
                      <span className={`veg-dot ${item.isVeg ? 'veg-dot--veg' : 'veg-dot--nveg'}`} />
                      <span className="menu-item__name">{item.name}</span>
                    </div>
                    {item.description && <p className="menu-item__desc">{item.description}</p>}
                    <div className="menu-item__foot">
                      <span className="menu-item__price">₹{item.price}</span>
                      <div style={{ display: 'flex', gap: 'var(--sp-2)' }}>
                        <button className="btn btn-outline btn-sm"
                          onClick={() => handleEditMenuItem(item)}>Edit</button>
                        <button className="btn btn-danger btn-sm"
                          onClick={() => handleDeleteMenuItem(item.id)}>Remove</button>
                      </div>
                    </div>
                  </div>
                ))}
                {menu.length === 0 && (
                  <p style={{ color: 'var(--text-muted)', fontSize: 'var(--fs-sm)' }}>No menu items yet.</p>
                )}
              </div>
            </div>
          )}
        </>
      )}

      {/* Incoming Orders tab */}
      {activeTab === 'Incoming Orders' && (
        <>
          {orders.length === 0 ? (
            <div className="empty-wrap">
              <div className="empty-icon">📋</div>
              <div className="empty-title">No orders yet</div>
            </div>
          ) : (
            orders.map(order => (
              <div key={order.id} className="order-card">
                <div className="order-card__head">
                  <div>
                    <div className="order-card__id">Order #{order.id}</div>
                    <div className="order-card__date">
                      {order.createdAt && new Date(order.createdAt).toLocaleString('en-IN')}
                    </div>
                    {order.deliveryAddress && (
                      <div style={{ fontSize: 'var(--fs-xs)', color: 'var(--text-3)' }}>📍 {order.deliveryAddress}</div>
                    )}
                  </div>
                  <span className="badge badge-orange">{order.status}</span>
                </div>
                <div className="order-card__body">
                  {(order.items || []).map((item, i) => (
                    <div key={i} className="order-item-row">
                      <span>{item.name} × {item.quantity}</span>
                      <span>₹{(item.price * item.quantity).toFixed(2)}</span>
                    </div>
                  ))}
                  <hr className="order-card__sep" />
                  <div className="order-card__total">
                    <span>Total</span><span>₹{order.totalAmount?.toFixed(2)}</span>
                  </div>
                </div>
                <div className="order-card__foot">
                  {order.status === 'PENDING' && (
                    <button className="btn btn-primary btn-sm"
                      onClick={() => handleOrderStatus(order.id, 'CONFIRMED')}>Confirm</button>
                  )}
                  {order.status === 'CONFIRMED' && (
                    <button className="btn btn-primary btn-sm"
                      onClick={() => handleOrderStatus(order.id, 'PREPARING')}>Start Preparing</button>
                  )}
                  {order.status === 'PREPARING' && (
                    <button className="btn btn-primary btn-sm"
                      onClick={() => handleOrderStatus(order.id, 'READY_FOR_PICKUP')}>Mark Ready</button>
                  )}
                </div>
              </div>
            ))
          )}
        </>
      )}
    </div>
  );
}
