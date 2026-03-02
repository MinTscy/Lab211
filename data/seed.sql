USE lab211_ecommerce;


INSERT INTO users (name, email, password_hash, role) VALUES
('Admin', 'admin@local', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ADMIN'),
('Test User', 'user@local', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'CUSTOMER');

INSERT INTO shops (name, owner_user_id, rating, active) VALUES
('NovaCart Official', 1, 4.80, 1),
('Streetwear House', 1, 4.65, 1),
('SmartLife Store', 1, 4.72, 1);

INSERT INTO products (shop_id, name, description, base_price, active) VALUES
(1, 'Wireless Earbuds', 'Noise-cancelling earbuds with charging case.', 29.99, 1),
(2, 'Streetwear Hoodie', 'Oversized hoodie in multiple colors.', 19.50, 1),
(3, 'Smart Bottle', 'Temperature display smart bottle.', 12.90, 1);

INSERT INTO product_variants (product_id, color, size, stock, price_delta) VALUES
(1, 'Black', 'Standard', 150, 0),
(1, 'White', 'Standard', 120, 0),
(2, 'Black', 'M', 80, 2.00),
(2, 'Black', 'L', 60, 3.00),
(2, 'Blue', 'M', 70, 2.00),
(3, 'Silver', '500ml', 200, 0),
(3, 'Silver', '750ml', 140, 1.50);

INSERT INTO vouchers (code, discount_type, discount_value, max_discount, min_order, start_at, end_at, active) VALUES
('FLASH10', 'PERCENT', 10, 15, 30, NOW() - INTERVAL 7 DAY, NOW() + INTERVAL 30 DAY, 1),
('SHIPFREE', 'FIXED', 5, 5, 20, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 7 DAY, 1);
