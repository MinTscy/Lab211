-- =========================
-- USERS (BUYERS)
-- =========================
INSERT INTO users (user_id, full_name, email, phone, created_at) VALUES
(1, 'Nguyễn Văn An', 'an.nguyen@gmail.com', '0987654321', '2024-01-05'),
(2, 'Trần Thị Bình', 'binh.tran@gmail.com', '0912345678', '2024-01-10'),
(3, 'Lê Hoàng', 'lehoanggmail.com', '097654321', '2024-01-15'), -- ❌ email sai + phone thiếu số
(4, NULL, 'minh.pham@gmail.com', '0901122334', '2024-01-20'), -- ❌ thiếu tên
(5, 'Phạm Quốc Cường', 'cuong.pham@gmail', '0933445566', '2024-01-25'); -- ❌ email sai domain

-- =========================
-- SELLERS
-- =========================
INSERT INTO sellers (seller_id, seller_name, phone, rating) VALUES
(1, 'Shop Điện Tử A', '0909998888', 4.8),
(2, 'Thời Trang B', '0911112222', 4.5),
(3, 'Gia Dụng C', '09222', 4.2); -- ❌ phone thiếu số

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO products (product_id, seller_id, product_name, price, stock) VALUES
(1, 1, 'Tai nghe Bluetooth', 350000, 100),
(2, 1, 'Chuột không dây', 0, 50), -- ❌ giá = 0
(3, 2, 'Áo thun nam', 199000, 200),
(4, 2, 'Quần jean nữ', -450000, 80), -- ❌ giá âm
(5, 3, 'Nồi chiên không dầu', 2200000, 30);

-- =========================
-- ORDERS
-- =========================
INSERT INTO orders (order_id, user_id, order_date, total_amount, status) VALUES
(1001, 1, '2024-02-01', 350000, 'COMPLETED'),
(1002, 2, '2024-02-02', 199000, 'COMPLETED'),
(1003, 3, '2024-02-03', 500000, 'COMPLETED'), -- ❌ total không khớp thực tế
(1004, 4, '2024-02-04', -199000, 'CANCELLED'), -- ❌ total âm
(1005, 5, '2024-02-05', 2200000, 'PENDING');

-- =========================
-- ORDER ITEMS
-- =========================
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1001, 1, 1, 350000),
(1002, 3, 1, 199000),
(1003, 1, 1, 350000), -- ❌ order 1003 total sai
(1004, 3, 1, 199000),
(1005, 5, 1, 2200000);
