import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getRestaurant, getMenu } from '../api/restaurants';
import { getReviews, addReview } from '../api/reviews';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';

export default function RestaurantDetail() {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu]             = useState([]);
  const [reviews, setReviews]       = useState([]);
  const [loading, setLoading]       = useState(true);
  const [reviewForm, setReviewForm] = useState({ rating: 0, comment: '' });
  const [submitting, setSubmitting] = useState(false);
  const [hovered, setHovered]       = useState(0);
  const { addItem } = useCart();
  const { user }    = useAuth();
  const navigate    = useNavigate();

  const loadReviews = useCallback(async () => {
    try {
      const res = await getReviews(id);
      const rd = res.data?.data || res.data;
      setReviews(Array.isArray(rd) ? rd : []);
    } catch {
      // reviews are non-critical — fail silently
    }
  }, [id]);

  useEffect(() => {
    const load = async () => {
      try {
        const [rRes, mRes] = await Promise.all([getRestaurant(id), getMenu(id)]);
        setRestaurant(rRes.data?.data || rRes.data);
        const md = mRes.data?.data || mRes.data;
        setMenu(Array.isArray(md) ? md : []);
      } catch {
        toast.error('Failed to load restaurant details');
      } finally {
        setLoading(false);
      }
    };
    load();
    loadReviews();
  }, [id, loadReviews]);

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (!user) { toast.info('Please login to leave a review'); navigate('/login'); return; }
    if (reviewForm.rating === 0) { toast.warn('Please select a star rating'); return; }
    setSubmitting(true);
    try {
      await addReview({
        restaurantId: Number(id),
        userId: user.id,
        userName: user.name,
        rating: reviewForm.rating,
        comment: reviewForm.comment,
      });
      toast.success('Review submitted!');
      setReviewForm({ rating: 0, comment: '' });
      loadReviews();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit review');
    } finally {
      setSubmitting(false);
    }
  };

  const handleAddToCart = async (item) => {
    if (!user) {
      toast.info('Please login to add items to cart');
      navigate('/login');
      return;
    }
    const result = await addItem({
      menuItemId: item.id,
      restaurantId: Number(id),
      restaurantName: restaurant?.name || '',
      name: item.name,
      price: item.price,
      quantity: 1,
    });
    if (result.success) toast.success(`${item.name} added to cart!`);
    else toast.error(result.message);
  };

  if (loading) return (
    <div className="loading-wrap" style={{ minHeight: 'calc(100vh - 62px)' }}>
      <div className="spinner" />
      <span>Loading restaurant…</span>
    </div>
  );

  if (!restaurant) return (
    <div className="empty-wrap">
      <div className="empty-icon">😕</div>
      <div className="empty-title">Restaurant not found</div>
    </div>
  );

  const byCategory = menu.reduce((acc, item) => {
    const cat = item.category || 'Menu';
    if (!acc[cat]) acc[cat] = [];
    acc[cat].push(item);
    return acc;
  }, {});

  return (
    <div className="container page">
      {/* Hero */}
      <div className="rd-hero">
        {restaurant.imageUrl
          ? <img src={restaurant.imageUrl} alt={restaurant.name} />
          : '🍽️'}
      </div>

      {/* Info */}
      <h1 className="rd-name">{restaurant.name}</h1>
      <p  className="rd-meta">{restaurant.cuisine}</p>
      {restaurant.address && <p className="rd-meta">📍 {restaurant.address}</p>}
      {restaurant.phone   && <p className="rd-meta">📞 {restaurant.phone}</p>}

      <div className="rd-badges">
        {restaurant.averageRating && (
          <span className="badge badge-green">⭐ {Number(restaurant.averageRating).toFixed(1)}</span>
        )}
        {restaurant.isOpen !== false
          ? <span className="badge badge-green">🟢 Open Now</span>
          : <span className="badge badge-red">🔴 Closed</span>}
        {restaurant.deliveryTime && (
          <span className="badge badge-orange">⏱ {restaurant.deliveryTime} min delivery</span>
        )}
        {restaurant.minOrder && (
          <span className="badge badge-grey">Min order ₹{restaurant.minOrder}</span>
        )}
      </div>

      {/* Menu */}
      {Object.entries(byCategory).map(([cat, items]) => (
        <div key={cat}>
          <div className="menu-cat-title">{cat}</div>
          <div className="menu-grid">
            {items.filter(i => i.isAvailable !== false).map((item) => (
              <div key={item.id} className="menu-item">
                <div className="menu-item__top">
                  <span className={`veg-dot ${item.isVeg ? 'veg-dot--veg' : 'veg-dot--nveg'}`} />
                  <span className="menu-item__name">{item.name}</span>
                </div>
                {item.description && (
                  <p className="menu-item__desc">{item.description}</p>
                )}
                <div className="menu-item__foot">
                  <span className="menu-item__price">₹{item.price}</span>
                  <button className="add-btn" onClick={() => handleAddToCart(item)}>
                    + Add
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}

      {menu.length === 0 && (
        <div className="empty-wrap">
          <div className="empty-icon">📋</div>
          <div className="empty-title">Menu not available</div>
          <p className="empty-text">This restaurant hasn't added any menu items yet.</p>
        </div>
      )}

      {/* ── Reviews Section ─────────────────────────────── */}
      <div className="reviews-section">
        <h2 className="reviews-title">Customer Reviews</h2>

        {/* Submit form */}
        <div className="review-form-card">
          <h3 className="review-form-heading">Write a Review</h3>
          <form onSubmit={handleSubmitReview}>
            {/* Star picker */}
            <div className="star-picker">
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
            <div className="form-group" style={{ marginTop: 12 }}>
              <textarea
                className="form-input"
                rows={3}
                placeholder="Share your experience (optional)"
                value={reviewForm.comment}
                onChange={e => setReviewForm(f => ({ ...f, comment: e.target.value }))}
                style={{ resize: 'vertical' }}
              />
            </div>
            <button className="btn btn-primary btn-sm" type="submit" disabled={submitting}>
              {submitting ? 'Submitting…' : 'Submit Review'}
            </button>
          </form>
        </div>

        {/* Reviews list */}
        {reviews.length === 0 ? (
          <p className="reviews-empty">No reviews yet. Be the first to review!</p>
        ) : (
          <div className="reviews-list">
            {reviews.map((r) => (
              <div key={r.id} className="review-card">
                <div className="review-card__head">
                  <span className="review-card__author">{r.userName || 'Customer'}</span>
                  <span className="review-card__stars">
                    {[1,2,3,4,5].map(s => (
                      <span key={s} className={s <= r.rating ? 'star star--filled' : 'star'}>★</span>
                    ))}
                  </span>
                </div>
                {r.comment && <p className="review-card__comment">{r.comment}</p>}
                {r.createdAt && (
                  <p className="review-card__date">
                    {new Date(r.createdAt).toLocaleDateString('en-IN', { dateStyle: 'medium' })}
                  </p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
