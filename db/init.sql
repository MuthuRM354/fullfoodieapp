-- FoodieApp Database Initialization
-- Run this script once on your MySQL server before starting any service
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS foodieapp_users
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_restaurants
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_orders
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_payments
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_delivery
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_notifications
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_reviews
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_tracking
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS foodieapp_admin
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Create application user with privileges on all databases
-- Replace 'yourpassword' with a strong password
CREATE USER IF NOT EXISTS 'foodieapp'@'%' IDENTIFIED BY 'yourpassword';

GRANT ALL PRIVILEGES ON foodieapp_users.*        TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_restaurants.*  TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_orders.*       TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_payments.*     TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_delivery.*     TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_notifications.*TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_reviews.*      TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_tracking.*     TO 'foodieapp'@'%';
GRANT ALL PRIVILEGES ON foodieapp_admin.*        TO 'foodieapp'@'%';

FLUSH PRIVILEGES;

SELECT 'All FoodieApp databases and user created successfully!' AS status;
