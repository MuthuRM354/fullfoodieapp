import React, { useState, useEffect } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { getRestaurants, searchRestaurants } from '../api/restaurants';
import { useAuth } from '../context/AuthContext';

export default function Home() {
  const { user } = useAuth();
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]   = useState('');
  const [query, setQuery]   = useState('');

  const fetchRestaurants = async (q = '') => {
    setLoading(true);
    setError('');
    try {
      const res = q ? await searchRestaurants(q) : await getRestaurants();
      const data = res.data;
      setRestaurants(Array.isArray(data?.data) ? data.data : Array.isArray(data) ? data : []);
    } catch {
      setError('Failed to load restaurants. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchRestaurants(); }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchRestaurants(query.trim());
  };

  const ratingBadge = (r) => {
    const val = r?.averageRating;
    if (!val) return null;
    return <span className="badge badge-green">⭐ {Number(val).toFixed(1)}</span>;
  };

  // Delivery partners don't browse/order food through this account —
  // send them straight to their own dashboard instead.
  if (user?.role === 'DELIVERY_PARTNER') {
    return <Navigate to="/delivery" replace />;
  }

  return (
    <>
      {/* ── Hero ─────────────────────────────────────────── */}
      <section className="hero">
        <div className="hero__inner">
          <span className="hero__eyebrow">🚀 Fast · Fresh · Delivered</span>
          <h1 className="hero__title">Hungry? We've&nbsp;got&nbsp;you. 🍕</h1>
          <p  className="hero__sub">Order from the best restaurants near you</p>

          <form className="hero__search" onSubmit={handleSearch}>
            <input
              className="hero__search-input"
              placeholder="Search restaurants or cuisines..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <button className="hero__search-btn" type="submit">Search</button>
          </form>

          <div className="hero__stats">
            <div className="hero__stat">
              <div className="hero__stat-value">500+</div>
              <div className="hero__stat-label">Restaurants</div>
            </div>
            <div className="hero__stat">
              <div className="hero__stat-value">30 min</div>
              <div className="hero__stat-label">Avg Delivery</div>
            </div>
            <div className="hero__stat">
              <div className="hero__stat-value">4.8★</div>
              <div className="hero__stat-label">Avg Rating</div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Restaurant grid ──────────────────────────────── */}
      <div className="section">
        <div className="section-header">
          <h2 className="section-title">
            {query ? `Results for "${query}"` : 'All Restaurants'}
          </h2>
          {!loading && !error && (
            <span className="section-count">{restaurants.length} places</span>
          )}
        </div>

        {loading && (
          <div className="loading-wrap">
            <div className="spinner" />
            <span>Loading restaurants…</span>
          </div>
        )}

        {error && <div className="error-text">{error}</div>}

        {!loading && !error && restaurants.length === 0 && (
          <div className="empty-wrap">
            <div className="empty-icon">🍽️</div>
            <div className="empty-title">No restaurants found</div>
            <p className="empty-text">
              {query ? `No results for "${query}". Try a different search.` : 'Check back soon — more restaurants are on the way!'}
            </p>
          </div>
        )}

        <div className="r-grid">
          {restaurants.map((r) => (
            <Link key={r.id} to={`/restaurants/${r.id}`} className="r-card">
              <div className="r-card__img">
                {r.imageUrl
                  ? <img src={r.imageUrl} alt={r.name} />
                  : '🍽️'}
                {r.isOpen === false && (
                  <div className="r-card__closed"><span>Closed</span></div>
                )}
              </div>
              <div className="r-card__body">
                <div className="r-card__name">{r.name}</div>
                <div className="r-card__meta">{[r.cuisine, r.address].filter(Boolean).join(' · ')}</div>
                <div className="r-card__badges">
                  {ratingBadge(r)}
                  {r.isOpen !== false && <span className="badge badge-green">Open</span>}
                  {r.deliveryTime && (
                    <span className="badge badge-orange">⏱ {r.deliveryTime} min</span>
                  )}
                  {r.minOrder && (
                    <span className="badge badge-grey">Min ₹{r.minOrder}</span>
                  )}
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </>
  );
}
