import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer__top">
        <div>
          <div className="footer__brand">🍔 FoodieApp</div>
          <p className="footer__desc">
            Discover the best food & drinks from restaurants near you. Fast delivery,
            fresh food — every time.
          </p>
        </div>

        <div>
          <div className="footer__col-title">Quick Links</div>
          <ul className="footer__links">
            <li><Link to="/">Home</Link></li>
            <li><Link to="/orders">My Orders</Link></li>
            <li><Link to="/profile">Profile</Link></li>
            <li><Link to="/cart">Cart</Link></li>
          </ul>
        </div>

        <div>
          <div className="footer__col-title">Company</div>
          <ul className="footer__links">
            <li><a href="#about">About Us</a></li>
            <li><a href="#careers">Careers</a></li>
            <li><a href="#privacy">Privacy Policy</a></li>
            <li><a href="#terms">Terms of Service</a></li>
          </ul>
        </div>
      </div>

      <div className="footer__bottom">
        © {new Date().getFullYear()} <strong>FoodieApp</strong> — Made with ❤️ for food lovers.
      </div>
    </footer>
  );
}
