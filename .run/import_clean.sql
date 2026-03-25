CREATE DATABASE IF NOT EXISTS lab211_ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab211_ecommerce;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE product_variants;
TRUNCATE TABLE products;
TRUNCATE TABLE shops;
TRUNCATE TABLE users;
TRUNCATE TABLE vouchers;
SET FOREIGN_KEY_CHECKS = 1;

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\users_clean.csv'
INTO TABLE users
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, name, email, password_hash, role, created_at);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\shops_clean.csv'
INTO TABLE shops
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, name, owner_user_id, rating, active, created_at);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\products_clean.csv'
INTO TABLE products
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, shop_id, name, description, base_price, active);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\product_variants_clean.csv'
INTO TABLE product_variants
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, product_id, color, size, stock, price_delta);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\vouchers_clean.csv'
INTO TABLE vouchers
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, code, discount_type, discount_value, max_discount, min_order, start_at, end_at, active);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\orders_clean.csv'
INTO TABLE orders
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, user_id, total_amount, discount_amount, final_amount, status, created_at);

LOAD DATA LOCAL INFILE 'C:\\\\Users\\\\triet\\\\lab211\\\\data\\\\clean\\\\order_items_clean.csv'
INTO TABLE order_items
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, order_id, variant_id, quantity, unit_price);

SELECT 'users' AS tbl, COUNT(*) AS cnt FROM users
UNION ALL SELECT 'shops', COUNT(*) FROM shops
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'product_variants', COUNT(*) FROM product_variants
UNION ALL SELECT 'vouchers', COUNT(*) FROM vouchers
UNION ALL SELECT 'orders', COUNT(*) FROM orders
UNION ALL SELECT 'order_items', COUNT(*) FROM order_items;
