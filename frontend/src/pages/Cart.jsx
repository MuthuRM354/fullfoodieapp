import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { placeOrder } from '../api/orders';
import { getProfile } from '../api/auth';
import { getAddresses } from '../api/addresses';
import { initiatePayment } from '../api/payments';

const PAYMENT_METHODS = [
  { value: 'CASH',   label: '💵 Cash on Delivery' },
  { value: 'UPI',    label: '📱 UPI' },
  { value: 'CARD',   label: '💳 Card' },
  { value: 'WALLET', label: '👛 Wallet' },
];

export default function Cart() {
  const { cart, updateItem, removeItem, clear, total } = useCart();
  const { user }    = useAuth();
  const navigate    = useNavigate();
  const [placing, setPlacing]         = useState(false);
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [paymentMethod, setPaymentMethod]     = useState('CASH');
  const [savedAddresses, setSavedAddresses]   = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);

  // Pre-fill delivery address from the saved address book — default address
  // wins, otherwise fall back to the legacy single profile address field.
  useEffect(() => {
    if (!user?.id) return;
    getAddresses(user.id)
      .then(res => {
        const data = res.data?.data || res.data;
        const list = Array.isArray(data) ? data : [];
        setSavedAddresses(list);
        const def = list.find(a => a.isDefault) || list[0];
        if (def) {
          setSelectedAddressId(def.id);
          setDeliveryAddress(def.addressLine);
          return;
        }
        // No saved addresses yet — fall back to the old profile address
        getProfile()
          .then(pRes => {
            const pData = pRes.data?.data || pRes.data;
            if (pData?.address) setDeliveryAddress(pData.address);
          })
          .catch(() => {});
      })
      .catch(() => {}); // non-critical — user can type manually
  }, [user?.id]);

  const handleSelectAddress = (addr) => {
    setSelectedAddressId(addr.id);
    setDeliveryAddress(addr.addressLine);
  };

  const items       = cart?.items || [];
  const deliveryFee = 40;
  const tax         = Math.round(total * 0.05);
  const grandTotal  = total + deliveryFee + tax;

  const handlePlaceOrder = async () => {
    if (items.length === 0) { toast.error('Your cart is empty'); return; }
    if (!deliveryAddress.trim()) { toast.error('Please enter a delivery address'); return; }
    setPlacing(true);
    try {
      const orderRes = await placeOrder({
        userId: user.id,
        deliveryAddress: deliveryAddress.trim(),
        totalAmount: grandTotal,
      });
      const order = orderRes.data?.data || orderRes.data;

      // Order is placed regardless of payment outcome (COD always succeeds;
      // card/UPI/wallet can fail — the order itself isn't rolled back, since
      // in a real app the customer would be asked to retry payment).
      try {
        const payRes = await initiatePayment({
          orderId: order?.id,
          userId: user.id,
          amount: grandTotal,
          paymentMethod,
        });
        const payment = payRes.data?.data || payRes.data;
        if (payRes.data?.success === false || payment?.status === 'FAILED') {
          toast.warn(payRes.data?.message || 'Order placed, but payment failed — you can retry from Payments.');
        }
      } catch {
        toast.warn('Order placed, but payment could not be processed right now.');
      }

      await clear();
      toast.success('Order placed successfully! 🎉');
      navigate('/orders');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to place order');
    } finally {
      setPlacing(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="container page">
        <h1 className="page-heading">Your Cart</h1>
        <div className="empty-wrap">
          <div className="empty-icon">🛒</div>
          <div className="empty-title">Cart is empty</div>
          <p className="empty-text">Add some delicious items to get started!</p>
          <Link to="/" className="btn btn-primary" style={{ marginTop: 8 }}>
            Browse Restaurants
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container page">
      <h1 className="page-heading">Your Cart</h1>
      <p  className="page-sub">{items.length} item{items.length !== 1 ? 's' : ''} in your cart</p>

      <div className="cart-layout">
        {/* Items */}
        <div className="cart-box">
          <div className="cart-box__head">
            <span className="cart-box__title">Items</span>
            <button
              className="btn btn-danger btn-sm"
              onClick={async () => { await clear(); toast.info('Cart cleared'); }}
            >
              Clear all
            </button>
          </div>

          {items.map((item, idx) => (
            <div key={item.id || idx} className="cart-item">
              <div className="cart-item__info">
                <div className="cart-item__name">{item.name}</div>
                <div className="cart-item__unit">₹{item.price} each</div>
              </div>

              <div className="qty-ctrl">
                <button
                  className="qty-btn"
                  onClick={() =>
                    item.quantity > 1
                      ? updateItem(item.id, item.quantity - 1)
                      : removeItem(item.id)
                  }
                >−</button>
                <span className="qty-val">{item.quantity}</span>
                <button className="qty-btn" onClick={() => updateItem(item.id, item.quantity + 1)}>+</button>
              </div>

              <div className="cart-item__price">₹{(item.price * item.quantity).toFixed(0)}</div>

              <button className="cart-item__rm" onClick={() => removeItem(item.id)} title="Remove">
                🗑
              </button>
            </div>
          ))}

          {/* Delivery address input inside the cart box */}
          <div style={{ padding: 'var(--sp-4) var(--sp-5)', borderTop: '1px solid var(--border)' }}>
            <label className="form-label">📍 Delivery Address</label>

            {savedAddresses.length > 0 && (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 10 }}>
                {savedAddresses.map(addr => (
                  <button
                    key={addr.id}
                    type="button"
                    className={`btn btn-sm ${selectedAddressId === addr.id ? 'btn-primary' : 'btn-outline'}`}
                    onClick={() => handleSelectAddress(addr)}
                    title={addr.addressLine}
                  >
                    {addr.label === 'Home' ? '🏠' : addr.label === 'Work' ? '🏢' : '📍'} {addr.label}
                  </button>
                ))}
              </div>
            )}

            <input
              className="form-input"
              type="text"
              placeholder="Enter your full delivery address"
              value={deliveryAddress}
              onChange={e => { setDeliveryAddress(e.target.value); setSelectedAddressId(null); }}
            />
            <p style={{ fontSize: 'var(--fs-xs)', color: 'var(--text-muted)', marginTop: 6 }}>
              Manage saved addresses from your <Link to="/profile">Profile</Link>.
            </p>
          </div>
        </div>

        {/* Summary */}
        <div className="cart-summary">
          <div className="cart-summary__title">Order Summary</div>

          <div className="cart-savings">🎉 Free delivery on your first order!</div>

          <div className="cart-summary__row">
            <span>Subtotal ({items.length} items)</span>
            <span>₹{total.toFixed(2)}</span>
          </div>
          <div className="cart-summary__row">
            <span>Delivery fee</span>
            <span>₹{deliveryFee}</span>
          </div>
          <div className="cart-summary__row">
            <span>Tax (5%)</span>
            <span>₹{tax}</span>
          </div>

          <hr className="cart-summary__sep" />

          <div className="cart-summary__total">
            <span>Total</span>
            <span>₹{grandTotal.toFixed(2)}</span>
          </div>

          <div style={{ margin: '12px 0' }}>
            <label className="form-label">Payment Method</label>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {PAYMENT_METHODS.map(m => (
                <button
                  key={m.value}
                  type="button"
                  className={`btn btn-sm ${paymentMethod === m.value ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => setPaymentMethod(m.value)}
                >
                  {m.label}
                </button>
              ))}
            </div>
          </div>

          <button
            className="btn btn-primary btn-full btn-lg"
            onClick={handlePlaceOrder}
            disabled={placing}
          >
            {placing ? 'Placing order…' : 'Place Order →'}
          </button>

          <p style={{ textAlign: 'center', fontSize: 'var(--fs-xs)', color: 'var(--text-muted)', marginTop: 12 }}>
            🔒 Secure checkout
          </p>
        </div>
      </div>
    </div>
  );
}
