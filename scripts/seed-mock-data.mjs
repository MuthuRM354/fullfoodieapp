#!/usr/bin/env node
// Populates FoodieApp with realistic demo data by calling the real REST APIs
// through the api-gateway (register/login, create restaurants, place orders,
// leave reviews, register delivery partners) — no direct DB access.
//
// Works against ANY environment, local or live: point API_BASE_URL at
// whichever api-gateway you want seeded.
//
// Usage:
//   node scripts/seed-mock-data.mjs                                  # http://localhost:8080
//   API_BASE_URL=https://your-live-gateway.onrender.com node scripts/seed-mock-data.mjs
//
// Re-runnable: users that already exist fall back to login instead of
// failing. Restaurants/menu items are NOT deduplicated — re-running creates
// a fresh set of restaurants alongside the previous ones (harmless for a
// demo, but don't loop this in CI).
//
// Paced at ~10 req/sec to stay well under the api-gateway's rate limiter
// (120 req/min per IP, see RateLimitFilter) — important when pointing this
// at a live deployment shared with real traffic.

const BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const PASSWORD = 'Demo@1234';
const REQUEST_DELAY_MS = 120;

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function api(method, path, body, token) {
  await sleep(REQUEST_DELAY_MS);
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  let json = null;
  try { json = await res.json(); } catch { /* empty body */ }
  if (!res.ok) {
    const message = json?.message || res.statusText;
    throw new Error(`${method} ${path} -> ${res.status}: ${message}`);
  }
  return json;
}

async function registerOrLogin(user) {
  try {
    const res = await api('POST', '/api/auth/register', {
      name: user.name,
      email: user.email,
      password: PASSWORD,
      phone: user.phone,
      role: user.role,
    });
    console.log(`  + registered ${user.email} (${user.role})`);
    return res.data;
  } catch (e) {
    const res = await api('POST', '/api/auth/login', { email: user.email, password: PASSWORD });
    console.log(`  = ${user.email} already existed, logged in`);
    return res.data;
  }
}

const OWNERS = [
  { name: 'Raj Kumar', email: 'raj.kumar@foodieapp.demo', phone: '9000000001', role: 'RESTAURANT_OWNER' },
  { name: 'Maria Rossi', email: 'maria.rossi@foodieapp.demo', phone: '9000000002', role: 'RESTAURANT_OWNER' },
  { name: 'Wei Chen', email: 'wei.chen@foodieapp.demo', phone: '9000000003', role: 'RESTAURANT_OWNER' },
];

const CUSTOMERS = [
  { name: 'Aditi Sharma', email: 'aditi.sharma@foodieapp.demo', phone: '9111111101', role: 'CUSTOMER' },
  { name: 'Rahul Verma', email: 'rahul.verma@foodieapp.demo', phone: '9111111102', role: 'CUSTOMER' },
  { name: 'Sneha Patel', email: 'sneha.patel@foodieapp.demo', phone: '9111111103', role: 'CUSTOMER' },
  { name: 'Karthik Iyer', email: 'karthik.iyer@foodieapp.demo', phone: '9111111104', role: 'CUSTOMER' },
];

const DELIVERY_PARTNERS = [
  { name: 'Vikram Singh', email: 'vikram.singh@foodieapp.demo', phone: '9222222201', role: 'DELIVERY_PARTNER', vehicleType: 'Bike', vehicleNumber: 'TN01AB1234' },
  { name: 'Arjun Nair', email: 'arjun.nair@foodieapp.demo', phone: '9222222202', role: 'DELIVERY_PARTNER', vehicleType: 'Scooter', vehicleNumber: 'KA02CD5678' },
];

// ownerIndex refers to OWNERS[]
const RESTAURANTS = [
  {
    ownerIndex: 0,
    name: 'Spice Junction', description: 'Authentic North & South Indian cuisine', cuisine: 'Indian',
    address: '12 Anna Salai', city: 'Chennai', phone: '9000000011', email: 'contact@spicejunction.demo',
    menu: [
      { name: 'Butter Chicken', description: 'Creamy tomato curry with tender chicken', price: 320, category: 'Main Course', isVeg: false },
      { name: 'Paneer Tikka', description: 'Chargrilled cottage cheese skewers', price: 250, category: 'Starters', isVeg: true },
      { name: 'Garlic Naan', description: 'Tandoor-baked flatbread with garlic', price: 60, category: 'Breads', isVeg: true },
      { name: 'Veg Biryani', description: 'Fragrant basmati rice with mixed vegetables', price: 220, category: 'Main Course', isVeg: true },
      { name: 'Masala Dosa', description: 'Crisp rice crepe with spiced potato filling', price: 130, category: 'South Indian', isVeg: true },
      { name: 'Gulab Jamun', description: 'Warm milk-solid dumplings in sugar syrup', price: 90, category: 'Desserts', isVeg: true },
    ],
  },
  {
    ownerIndex: 1,
    name: 'Bella Italia', description: 'Wood-fired pizza and handmade pasta', cuisine: 'Italian',
    address: '45 MG Road', city: 'Chennai', phone: '9000000012', email: 'contact@bellaitalia.demo',
    menu: [
      { name: 'Margherita Pizza', description: 'San Marzano tomato, mozzarella, basil', price: 350, category: 'Pizza', isVeg: true },
      { name: 'Pasta Alfredo', description: 'Fettuccine in creamy parmesan sauce', price: 300, category: 'Pasta', isVeg: true },
      { name: 'Garlic Bread', description: 'Toasted baguette with garlic butter', price: 120, category: 'Starters', isVeg: true },
      { name: 'Caesar Salad', description: 'Romaine, parmesan, croutons, grilled chicken', price: 220, category: 'Salads', isVeg: false },
      { name: 'Tiramisu', description: 'Espresso-soaked ladyfingers, mascarpone', price: 180, category: 'Desserts', isVeg: true },
    ],
  },
  {
    ownerIndex: 2,
    name: 'Dragon Wok', description: 'Wok-tossed Indo-Chinese favourites', cuisine: 'Chinese',
    address: '78 Brigade Road', city: 'Bangalore', phone: '9000000013', email: 'contact@dragonwok.demo',
    menu: [
      { name: 'Veg Manchurian', description: 'Fried vegetable dumplings in tangy sauce', price: 210, category: 'Starters', isVeg: true },
      { name: 'Hakka Noodles', description: 'Stir-fried noodles with vegetables', price: 190, category: 'Main Course', isVeg: true },
      { name: 'Spring Rolls', description: 'Crispy rolls with vegetable filling', price: 150, category: 'Starters', isVeg: true },
      { name: 'Kung Pao Chicken', description: 'Spicy stir-fried chicken with peanuts', price: 280, category: 'Main Course', isVeg: false },
      { name: 'Fried Rice', description: 'Wok-tossed rice with egg and vegetables', price: 180, category: 'Main Course', isVeg: false },
    ],
  },
  {
    ownerIndex: 2,
    name: 'Sushi Sakura', description: 'Fresh sushi and Japanese comfort food', cuisine: 'Japanese',
    address: '90 Indiranagar', city: 'Bangalore', phone: '9000000014', email: 'contact@sushisakura.demo',
    menu: [
      { name: 'California Roll', description: 'Crab, avocado, cucumber', price: 380, category: 'Sushi', isVeg: false },
      { name: 'Salmon Nigiri', description: 'Fresh salmon over pressed rice', price: 420, category: 'Sushi', isVeg: false },
      { name: 'Miso Soup', description: 'Traditional fermented soybean broth', price: 140, category: 'Soups', isVeg: true },
      { name: 'Chicken Teriyaki', description: 'Grilled chicken in teriyaki glaze', price: 350, category: 'Main Course', isVeg: false },
      { name: 'Edamame', description: 'Steamed and salted soybeans', price: 120, category: 'Starters', isVeg: true },
    ],
  },
];

const REVIEW_COMMENTS = [
  { rating: 5, comment: 'Absolutely loved it, will order again!' },
  { rating: 4, comment: 'Great taste, delivery took a little while.' },
  { rating: 5, comment: 'Best in the neighbourhood, highly recommend.' },
  { rating: 3, comment: 'Food was good but portion size was small.' },
  { rating: 4, comment: 'Fresh ingredients and quick delivery.' },
];

function pick(arr, i) { return arr[i % arr.length]; }

async function main() {
  console.log(`Seeding mock data into ${BASE_URL}\n`);

  console.log('Registering restaurant owners...');
  const ownerAuth = [];
  for (const o of OWNERS) ownerAuth.push(await registerOrLogin(o));

  console.log('\nRegistering customers...');
  const customerAuth = [];
  for (const c of CUSTOMERS) customerAuth.push(await registerOrLogin(c));

  console.log('\nRegistering delivery partners...');
  const partnerAuth = [];
  for (const d of DELIVERY_PARTNERS) partnerAuth.push(await registerOrLogin(d));
  for (let i = 0; i < DELIVERY_PARTNERS.length; i++) {
    try {
      await api('POST', '/api/delivery/partners', {
        userId: partnerAuth[i].userId,
        name: DELIVERY_PARTNERS[i].name,
        phone: DELIVERY_PARTNERS[i].phone,
        vehicleType: DELIVERY_PARTNERS[i].vehicleType,
        vehicleNumber: DELIVERY_PARTNERS[i].vehicleNumber,
        isAvailable: true,
      }, partnerAuth[i].token);
      console.log(`  + delivery partner profile created for ${DELIVERY_PARTNERS[i].name}`);
    } catch (e) {
      console.log(`  ~ delivery partner profile skipped for ${DELIVERY_PARTNERS[i].name}: ${e.message}`);
    }
  }

  console.log('\nCreating restaurants + menus...');
  const restaurantIds = [];
  const restaurantMenus = []; // parallel array: created menu items (with real ids) per restaurant
  for (const r of RESTAURANTS) {
    const owner = ownerAuth[r.ownerIndex];
    const created = await api('POST', '/api/restaurants', {
      name: r.name, description: r.description, cuisine: r.cuisine,
      address: r.address, city: r.city, phone: r.phone, email: r.email,
      ownerId: owner.userId,
    }, owner.token);
    const restaurantId = created.data.id;
    restaurantIds.push(restaurantId);
    console.log(`  + ${r.name} (id=${restaurantId})`);

    const menuItems = [];
    for (const item of r.menu) {
      const createdItem = await api('POST', `/api/restaurants/${restaurantId}/menu`, item, owner.token);
      menuItems.push(createdItem.data);
    }
    restaurantMenus.push(menuItems);
    console.log(`    + ${r.menu.length} menu items`);
  }

  console.log('\nPlacing orders...');
  const placedOrders = [];
  for (let i = 0; i < customerAuth.length; i++) {
    const customer = customerAuth[i];
    const restaurant = RESTAURANTS[i % RESTAURANTS.length];
    const restaurantId = restaurantIds[i % restaurantIds.length];
    const menuItems = restaurantMenus[i % restaurantMenus.length];
    const items = [menuItems[0], pick(menuItems, i + 1)];

    for (const item of items) {
      await api('POST', `/api/cart/${customer.userId}/items`, {
        menuItemId: item.id, name: item.name, price: item.price, quantity: 1,
        restaurantId, restaurantName: restaurant.name,
      }, customer.token);
    }

    const order = await api('POST', '/api/orders', {
      userId: customer.userId,
      deliveryAddress: `${100 + i} Demo Street, ${restaurant.city}`,
    }, customer.token);
    placedOrders.push({ id: order.data.id, token: customer.token, restaurantId, customerName: CUSTOMERS[i].name });
    console.log(`  + order #${order.data.id} for ${CUSTOMERS[i].name} from ${restaurant.name}`);
  }

  console.log('\nAdvancing some order statuses for variety...');
  const statuses = ['CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
  for (let i = 0; i < placedOrders.length; i++) {
    const targetStatus = statuses[i % statuses.length];
    try {
      // Walk through intermediate statuses so any status-based side effects fire.
      const chain = statuses.slice(0, statuses.indexOf(targetStatus) + 1);
      for (const s of chain) {
        await api('PUT', `/api/orders/${placedOrders[i].id}/status`, { status: s }, placedOrders[i].token);
      }
      console.log(`  + order #${placedOrders[i].id} -> ${targetStatus}`);
    } catch (e) {
      console.log(`  ~ order #${placedOrders[i].id} status update skipped: ${e.message}`);
    }
  }

  console.log('\nSubmitting reviews...');
  for (let i = 0; i < customerAuth.length; i++) {
    const customer = customerAuth[i];
    const restaurantId = restaurantIds[i % restaurantIds.length];
    const review = pick(REVIEW_COMMENTS, i);
    try {
      await api('POST', '/api/reviews', {
        userId: customer.userId, restaurantId, rating: review.rating, comment: review.comment,
      }, customer.token);
      console.log(`  + ${CUSTOMERS[i].name} rated restaurant ${restaurantId}: ${review.rating}★`);
    } catch (e) {
      console.log(`  ~ review skipped: ${e.message}`);
    }
  }

  console.log('\nDone. Demo accounts (password for all: Demo@1234):');
  for (const o of OWNERS) console.log(`  owner:    ${o.email}`);
  for (const c of CUSTOMERS) console.log(`  customer: ${c.email}`);
  for (const d of DELIVERY_PARTNERS) console.log(`  delivery: ${d.email}`);
}

main().catch((e) => {
  console.error('\nSeeding failed:', e.message);
  process.exit(1);
});
