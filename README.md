<<<<<<< HEAD
# LAB211 - E-Commerce Simulation (Shopee/Lazada)
=======
# LAB211 - E-Commerce Simulation
>>>>>>> 4bf8001 (Update all curent changes)

Đây là đồ án LAB211 mô phỏng sàn thương mại điện tử theo mô hình Shopee/Lazada. Hệ thống gồm:

- `src/core_app`: ứng dụng Java Web dùng Servlet/JSP chạy trên Tomcat
- `src/simulator`: chương trình Java console dùng để gửi request mô phỏng đặt hàng đồng thời
- `data`: schema SQL, seed mẫu và bộ CSV đã làm sạch để import dữ liệu
- `docs`: sơ đồ phân tích và nhật ký sử dụng AI

<<<<<<< HEAD
```
/Student_Project
+-- /data
+-- /src
¦   +-- /core_app
¦   +-- /simulator
+-- /docs
¦   +-- /analysis
¦   +-- /ai_logs
+-- README.md
=======
## 1. Mục tiêu project

Project bám theo 4 yêu cầu chính của môn:

- `Migrate`: chuẩn hóa dữ liệu CSV và chuẩn bị dữ liệu để import vào MySQL local
- `Operate`: xây dựng web app quản lý người dùng, sản phẩm, giỏ hàng, thanh toán
- `Simulate`: tạo tool gửi nhiều HTTP request để test tải cho Tomcat
- `Visualize`: hiển thị giao diện storefront, product detail, cart, checkout và trang quản trị cơ bản

## 2. Cấu trúc thư mục

```text
lab211/
|-- data/
|   |-- schema.sql
|   |-- seed.sql
|   `-- clean/
|-- docs/
|   |-- analysis/
|   `-- ai_logs/
|-- src/
|   |-- core_app/
|   `-- simulator/
`-- README.md
>>>>>>> 4bf8001 (Update all curent changes)
```

## 3. Công nghệ sử dụng

- Java Servlet/JSP
- Apache Tomcat
- MySQL local
- JDBC
- Maven
- Gson cho JSON API
- Java `HttpClient` trong simulator

## 4. Kiến trúc hệ thống

### Project A - Core Web App

Ứng dụng web trong `src/core_app` triển khai theo hướng MVC đơn giản:

- `model`: biểu diễn các thực thể như `User`, `Shop`, `Product`, `ProductVariant`, `Order`, `Voucher`, `ProductReview`
- `dao`: thao tác dữ liệu với MySQL qua JDBC
- `service`: gom business logic đặt hàng và áp mã giảm giá
- `servlet`: xử lý request web và API
- `webapp/WEB-INF/views`: giao diện JSP

Các endpoint chính:

- `/home`, `/products`, `/product`
- `/login`, `/register`, `/logout`, `/profile`
- `/cart`, `/checkout`
- `/review`
- `/admin/products`, `/admin/vouchers`
- `/seller/register`, `/seller/dashboard`, `/seller/product/new`
- `/api/health`, `/api/orders`

### Project B - Simulator

`src/simulator` là ứng dụng console gửi request đến:

- `/login` để lấy session
- `/api/health` để warm up
- `/api/orders` để mô phỏng nhiều đơn hàng đồng thời

Simulator cho phép cấu hình server, số order, độ đồng thời, danh sách variant và voucher bằng command line.

## 5. Thiết kế cơ sở dữ liệu

Schema hiện tại nằm ở [data/schema.sql](/c:/Users/triet/lab211/data/schema.sql), gồm các bảng:

- `users`
- `shops`
- `products`
- `product_variants`
- `vouchers`
- `orders`
- `order_items`
- `product_reviews`

Quan hệ chính:

- `shops.owner_user_id -> users.id`
- `products.shop_id -> shops.id`
- `product_variants.product_id -> products.id`
- `vouchers.product_id -> products.id` (nullable, hỗ trợ voucher toàn shop/toàn giỏ theo logic hiện tại)
- `orders.user_id -> users.id`
- `order_items.order_id -> orders.id`
- `order_items.variant_id -> product_variants.id`
- `product_reviews.product_id -> products.id`
- `product_reviews.user_id -> users.id`

## 6. Dữ liệu mẫu và dữ liệu làm sạch

Ngoài `seed.sql` để demo nhanh, project còn có bộ CSV đã làm sạch trong `data/clean`:

- `users_clean.csv`: 1000 dòng
- `shops_clean.csv`: 1000 dòng
- `products_clean.csv`: 200 dòng
- `product_variants_clean.csv`: 600 dòng
- `orders_clean.csv`: 2000 dòng
- `order_items_clean.csv`: 12000 dòng
- `vouchers_clean.csv`: 10 dòng

Thứ tự import khuyến nghị để tránh lỗi khóa ngoại:

1. `users_clean.csv`
2. `shops_clean.csv`
3. `products_clean.csv`
4. `product_variants_clean.csv`
5. `orders_clean.csv`
6. `order_items_clean.csv`
7. `vouchers_clean.csv`

## 7. Chức năng đã hoàn thành

### Người dùng

- Đăng ký, đăng nhập, đăng xuất
- Cập nhật hồ sơ cá nhân
- Lưu session người dùng

### Storefront

- Trang danh sách sản phẩm ưu tiên sản phẩm từ shop rating cao
- Tìm kiếm theo tên/mô tả để truy cập toàn bộ catalog
- Trang chi tiết sản phẩm với chọn variant, số lượng và xem review
- UI theo phong cách sàn thương mại điện tử

### Giỏ hàng và thanh toán

- Giỏ hàng theo session
- Chọn biến thể sản phẩm theo size/màu
- Áp voucher toàn đơn hoặc voucher theo sản phẩm
- Tạo order và order items khi checkout thành công

### Quản trị và seller

- Quản lý sản phẩm phía admin
- Quản lý voucher phía admin
- Đăng ký seller và thêm sản phẩm seller
- Seller dashboard

### Review

- Chỉ người dùng đã mua mới được đánh giá sản phẩm
- Một người dùng chỉ có một review cho mỗi sản phẩm, có thể cập nhật lại

### API và mô phỏng tải

- `GET /api/health` kiểm tra trạng thái ứng dụng
- `POST /api/orders` nhận JSON order để simulator hoặc client khác gọi

## 8. Business logic quan trọng

### Checkout có transaction

Logic đặt hàng trong [OrderService.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/service/OrderService.java) và [OrderDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/OrderDAO.java):

- Kiểm tra user còn tồn tại
- Kiểm tra product, shop còn active
- Kiểm tra stock theo variant
- Tính tổng tiền, voucher hợp lệ, mức giảm và số tiền cuối
- Tạo `orders` và `order_items` trong cùng transaction
- Trừ kho trong transaction để tránh oversell
- Có retry ngắn khi phát sinh race condition ở bước trừ kho

### Voucher

Hệ thống hỗ trợ 2 loại:

- Voucher toàn đơn: `product_id = NULL`
- Voucher áp cho một sản phẩm cụ thể: `product_id` trỏ tới `products.id`

Voucher được kiểm tra theo:

- mã còn active
- thời gian hiệu lực
- ngưỡng `min_order`
- `discount_type` là `PERCENT` hoặc `FIXED`
- `max_discount`

## 9. Hướng dẫn chạy project

### Yêu cầu môi trường

- MySQL local
- Tomcat 9+
- JDK 8 cho `core_app`
- JDK 17 cho `simulator`
- Maven

### Cấu hình database

Thông tin mặc định hiện tại trong [db.properties](/c:/Users/triet/lab211/src/core_app/src/main/resources/db.properties):

- Database: `lab211_ecommerce`
- User: `root`
- Password: `123456`
- Port: `3306`

Khởi tạo database:

```sql
source data/schema.sql;
source data/seed.sql;
```

<<<<<<< HEAD
Database name: `lab211_ecommerce`
User: `root`
Password: `123456`
Host: `localhost`
Port: `3306`
=======
Nếu muốn dùng bộ CSV lớn, hãy import dữ liệu trong `data/clean` theo đúng thứ tự ở phần trên.
>>>>>>> 4bf8001 (Update all curent changes)

### Chạy core_app

1. Import `src/core_app` như Maven WAR project.
2. Build project.
3. Deploy lên Tomcat.
4. Truy cập:

```text
http://localhost:8080/ecommerce-sim
```

<<<<<<< HEAD
## Notes
- Update DB settings in `src/core_app/src/main/resources/db.properties` if needed.
- Simulator (Project B) is not yet implemented.
=======
Tài khoản mẫu trong `seed.sql`:

- Admin: `admin@local / 123456`
- Customer: `user@local / 123456`
>>>>>>> 4bf8001 (Update all curent changes)

Voucher mẫu:

- `FLASH10`
- `EARBUD5`
- `HOODIE15`

### Chạy simulator

Build `src/simulator` thành file JAR rồi chạy ví dụ:

```bash
java -jar target/ecommerce-simulator-1.0.0.jar --server=http://localhost:8080/ecommerce-sim --orders=100 --concurrency=10 --variants=1,2,3 --email=user@local --password=123456 --voucher=FLASH10
```

Simulator sẽ:

- đăng nhập để lấy session
- gọi `GET /api/health`
- gửi nhiều request `POST /api/orders`
- in ra số request thành công/thất bại và thời gian trung bình

## 10. Tài liệu đi kèm

- Sơ đồ phân tích: [docs/analysis/flowchart.md](/c:/Users/triet/lab211/docs/analysis/flowchart.md)
- Sơ đồ lớp: [docs/analysis/class_diagram.png](/c:/Users/triet/lab211/docs/analysis/class_diagram.png)
- Sơ đồ hệ thống: [docs/analysis/diagram.svg](/c:/Users/triet/lab211/docs/analysis/diagram.svg)
- AI log: [docs/ai_logs/log.md](/c:/Users/triet/lab211/docs/ai_logs/log.md)

## 11. Ghi chú

- Thư mục `target/` là output build, không phải source chính để chỉnh sửa.
- `README` này mô tả theo mã nguồn hiện tại trong repo.
- Nếu đổi cấu hình DB, cần cập nhật lại `db.properties` trước khi deploy.
