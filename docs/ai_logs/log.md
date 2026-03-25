# AI Log - LAB211 E-Commerce Simulation

## 1. Mục đích tài liệu

Tài liệu này ghi lại quá trình sử dụng AI trong khi xây dựng đồ án LAB211. AI được dùng như công cụ hỗ trợ phân tích, gợi ý code, gợi ý dữ liệu mẫu, và hỗ trợ chỉnh giao diện. Nhóm vẫn kiểm tra, chỉnh sửa và tích hợp lại vào project trước khi sử dụng.

Nguồn tham chiếu chính để tổng hợp log này:

- lịch sử trao đổi lưu trong `docs/ai_logs/Ai Log.txt`
- mã nguồn hiện tại trong `src/core_app`
- mã nguồn hiện tại trong `src/simulator`
- dữ liệu trong `data/clean`

## 2. Nguyên tắc sử dụng AI

- AI chỉ đóng vai trò hỗ trợ, không thay thế việc hiểu code
- Mọi đoạn gợi ý từ AI đều được đọc lại, chỉnh sửa và gắn vào cấu trúc project hiện có
- Các quyết định về schema, luồng nghiệp vụ và giao diện được đối chiếu lại với yêu cầu môn học

## 3. Tóm tắt các mảng công việc có AI hỗ trợ

1. Chuẩn hóa dữ liệu CSV để phục vụ import MySQL
2. Gợi ý schema và quan hệ giữa các bảng
3. Xây dựng web core bằng Servlet/JSP
4. Viết logic đặt hàng, voucher, transaction và chống âm kho
5. Tạo API để simulator gọi
6. Viết simulator gửi request đồng thời
7. Chỉnh UI storefront theo mẫu tham khảo
8. Hoàn thiện review, profile, admin voucher và seller flow

## 4. Nhật ký chi tiết

### Log 01 - Định hình đề tài và cấu trúc repo

**Vấn đề**

Nhóm cần chọn đề tài đúng format LAB211 và tổ chức repo theo cấu trúc mà giảng viên yêu cầu.

**Prompt/Tóm tắt yêu cầu với AI**

Yêu cầu AI đọc đề môn học, chọn đề tài số 1 là E-Commerce Simulation và nhắc lại cấu trúc repo chuẩn.

**Gợi ý từ AI**

- Chọn đề tài E-Commerce Simulation
- Chia project thành `core_app` và `simulator`
- Tạo thêm `data`, `docs/analysis`, `docs/ai_logs`

**Sinh viên đã làm gì**

- Tổ chức repo theo đúng yêu cầu môn
- Tách source web và simulator thành 2 Maven project riêng
- Bổ sung thư mục tài liệu và dữ liệu

**Kết quả áp dụng**

Repo hiện có đầy đủ `data`, `src/core_app`, `src/simulator`, `docs/analysis`, `docs/ai_logs`.

### Log 02 - Thiết kế schema MySQL cho bài toán e-commerce

**Vấn đề**

Ban đầu cần xác định những bảng tối thiểu để mô phỏng người dùng, shop, sản phẩm, biến thể, đơn hàng và voucher.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI gợi ý schema cho hệ thống giống Shopee/Lazada chạy local bằng MySQL.

**Gợi ý từ AI**

- Tạo các bảng `users`, `shops`, `products`, `product_variants`, `orders`, `order_items`, `vouchers`
- Sử dụng khóa ngoại để ràng buộc dữ liệu
- Tách `order` và `order_item` thay vì lưu thẳng cart vào DB

**Sinh viên đã làm gì**

- Rà soát lại quan hệ cho phù hợp bài toán
- Bổ sung bảng `product_reviews`
- Chọn phương án `vouchers.product_id` là khóa ngoại nullable để hỗ trợ voucher toàn đơn hoặc voucher theo sản phẩm

**File liên quan**

- [data/schema.sql](/c:/Users/triet/lab211/data/schema.sql)

### Log 03 - Làm sạch dữ liệu CSV và xử lý import

**Vấn đề**

Dữ liệu CSV ban đầu chưa đồng đều, có chỗ để trống, có chỗ giá trị chưa thực tế, dễ gây lỗi khi import vào MySQL.

**Prompt/Tóm tắt yêu cầu với AI**

Nhiều yêu cầu nhỏ được đưa cho AI:

- giải thích thứ tự import
- giải thích lỗi import 0 records
- chỉnh tên người dùng, email, shop name cho thực tế hơn
- thêm timestamp ngẫu nhiên
- mở rộng số lượng user/shop
- chỉnh tên và mô tả sản phẩm cho hợp lý
- làm cho dữ liệu đơn hàng thực tế hơn

**Gợi ý từ AI**

- Import theo thứ tự cha trước con sau để tránh lỗi khóa ngoại
- Cho `created_at` dùng mặc định hoặc sinh ngẫu nhiên
- Tăng `users_clean.csv` và `shops_clean.csv` để khớp quan hệ chủ shop
- Chuẩn hóa `product_variants` bằng `Standard/N-A` cho mặt hàng không có size màu thật

**Sinh viên đã làm gì**

- Chỉnh và giữ lại bộ CSV sạch trong `data/clean`
- Tăng dữ liệu lên quy mô lớn hơn dữ liệu seed
- Kiểm tra lại khóa ngoại và tính hợp lệ trước khi import

**Kết quả dữ liệu sạch hiện có**

- `users_clean.csv`: 1000 dòng
- `shops_clean.csv`: 1000 dòng
- `products_clean.csv`: 200 dòng
- `product_variants_clean.csv`: 600 dòng
- `orders_clean.csv`: 2000 dòng
- `order_items_clean.csv`: 12000 dòng
- `vouchers_clean.csv`: 10 dòng

**File liên quan**

- [data/clean/users_clean.csv](/c:/Users/triet/lab211/data/clean/users_clean.csv)
- [data/clean/shops_clean.csv](/c:/Users/triet/lab211/data/clean/shops_clean.csv)
- [data/clean/products_clean.csv](/c:/Users/triet/lab211/data/clean/products_clean.csv)
- [data/clean/product_variants_clean.csv](/c:/Users/triet/lab211/data/clean/product_variants_clean.csv)
- [data/clean/orders_clean.csv](/c:/Users/triet/lab211/data/clean/orders_clean.csv)
- [data/clean/order_items_clean.csv](/c:/Users/triet/lab211/data/clean/order_items_clean.csv)
- [data/clean/vouchers_clean.csv](/c:/Users/triet/lab211/data/clean/vouchers_clean.csv)

### Log 04 - Scaffold web core với Servlet/JSP

**Vấn đề**

Nhóm cần nhanh chóng có bộ khung web chạy được trên Tomcat và kết nối được với MySQL.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI scaffold web core Java Web dùng Servlet/JSP, JDBC, Maven WAR.

**Gợi ý từ AI**

- Tạo Maven WAR project
- Tạo lớp `DBUtil`, `PasswordUtil`
- Tạo DAO, model, servlet và JSP cơ bản cho auth, product, cart, checkout, admin

**Sinh viên đã làm gì**

- Tích hợp khung gợi ý vào cấu trúc package `com.lab211.ecommerce`
- Chỉnh lại cấu hình kết nối DB
- Bổ sung filter xác thực
- Tạo giao diện JSP tương ứng cho từng flow

**File liên quan**

- [src/core_app/pom.xml](/c:/Users/triet/lab211/src/core_app/pom.xml)
- [src/core_app/src/main/java/com/lab211/ecommerce/util/DBUtil.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/util/DBUtil.java)
- [src/core_app/src/main/resources/db.properties](/c:/Users/triet/lab211/src/core_app/src/main/resources/db.properties)

### Log 05 - Xây dựng xác thực và quản lý người dùng

**Vấn đề**

Cần hoàn thiện flow đăng ký, đăng nhập, đăng xuất và hồ sơ cá nhân.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI gợi ý CRUD và servlet cho người dùng cùng cách băm mật khẩu.

**Gợi ý từ AI**

- Dùng session để giữ người dùng đăng nhập
- Tạo `LoginServlet`, `RegisterServlet`, `LogoutServlet`, `ProfileServlet`
- Dùng SHA-256 để băm mật khẩu

**Sinh viên đã làm gì**

- Tích hợp `PasswordUtil`
- Kiểm tra trùng email khi cập nhật profile
- Bắt buộc nhập đúng mật khẩu cũ nếu muốn đổi mật khẩu

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/LoginServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/LoginServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/RegisterServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/RegisterServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProfileServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProfileServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/filter/AuthFilter.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/filter/AuthFilter.java)

### Log 06 - Danh sách sản phẩm, tìm kiếm và ưu tiên shop rating cao

**Vấn đề**

Người dùng muốn giao diện giống sàn thương mại điện tử, không hiển thị toàn bộ 200 sản phẩm ở trang đầu, nhưng vẫn tìm kiếm được toàn bộ catalog.

**Prompt/Tóm tắt yêu cầu với AI**

Yêu cầu UI storefront theo mẫu tham khảo, ưu tiên hiển thị sản phẩm nổi bật từ shop rating cao, còn toàn bộ sản phẩm phải tìm kiếm được.

**Gợi ý từ AI**

- Tạo truy vấn lấy featured products theo `shop.rating`
- Giới hạn sản phẩm trang chủ
- Khi có `?q=...` thì tìm toàn bộ theo tên/mô tả

**Sinh viên đã làm gì**

- Áp dụng logic featured ở `ProductListServlet`
- Mở rộng model `Product` và query trong `ProductDAO`
- Kết hợp variant vào danh sách sản phẩm để phục vụ quick view/detail

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProductListServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProductListServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/dao/ProductDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/ProductDAO.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/model/Product.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/model/Product.java)

### Log 07 - Chi tiết sản phẩm và chọn variant

**Vấn đề**

Người dùng không muốn nút "View Detail" riêng mà muốn bấm vào ảnh sản phẩm để chọn màu, size và số lượng.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI sửa storefront để click ảnh sản phẩm sẽ mở chi tiết hoặc quick view giống mẫu tham khảo.

**Gợi ý từ AI**

- Tạo `ProductDetailServlet`
- Thêm `product_detail.jsp`
- Đưa variant vào modal hoặc detail page
- Dùng ảnh hoặc placeholder để tăng tính trực quan

**Sinh viên đã làm gì**

- Giữ cả detail page và quick view phù hợp với JSP app
- Gắn dữ liệu variant cho từng product card
- Chỉnh CSS để đồng bộ với bố cục storefront

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProductDetailServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/ProductDetailServlet.java)
- [src/core_app/src/main/webapp/WEB-INF/views/products.jsp](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/products.jsp)
- [src/core_app/src/main/webapp/WEB-INF/views/product_detail.jsp](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/product_detail.jsp)
- [src/core_app/src/main/webapp/assets/css/app.css](/c:/Users/triet/lab211/src/core_app/src/main/webapp/assets/css/app.css)

### Log 08 - Giỏ hàng, voucher và checkout transaction

**Vấn đề**

Bài toán khó nhất là tính tiền đúng, áp voucher đúng điều kiện và không để âm kho khi nhiều request cùng đặt hàng.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI gợi ý service/DAO cho checkout, transaction JDBC và cách trừ kho an toàn.

**Gợi ý từ AI**

- Tạo `OrderService` để gom validate và tính tiền
- Tạo `OrderDAO.createOrder(...)` để insert order, order items và trừ kho trong cùng transaction
- Kiểm tra voucher theo loại, thời gian, min order, max discount

**Sinh viên đã làm gì**

- Kiểm tra thêm user, product, shop đều còn hợp lệ trước khi tạo đơn
- Chặn mua vượt stock
- Thêm retry ngắn khi có race condition ở bước giảm kho
- Dùng session cart cho flow web và API riêng cho simulator

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/service/OrderService.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/service/OrderService.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/dao/OrderDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/OrderDAO.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/dao/VoucherDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/VoucherDAO.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/CartServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/CartServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/CheckoutServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/CheckoutServlet.java)

### Log 09 - API đặt hàng cho simulator

**Vấn đề**

Simulator cần một API riêng để gửi JSON thay vì đi qua toàn bộ form web.

**Prompt/Tóm tắt yêu cầu với AI**

Yêu cầu AI gợi ý endpoint JSON để tạo đơn hàng từ danh sách variant và voucher.

**Gợi ý từ AI**

- Tạo `POST /api/orders`
- Dùng Gson để parse body JSON
- Trả JSON phản hồi gồm trạng thái, message và orderId

**Sinh viên đã làm gì**

- Thêm kiểm tra session user trước khi cho phép gọi API
- Chuẩn hóa response lỗi cho các trường hợp JSON sai, thiếu quyền hoặc lỗi nghiệp vụ

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/api/OrderApiServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/api/OrderApiServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/api/HealthServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/api/HealthServlet.java)

### Log 10 - Viết simulator bắn request đồng thời

**Vấn đề**

Môn học yêu cầu phải có Project B để mô phỏng tải và stress test hệ thống.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI viết Java console app dùng `HttpClient` để đăng nhập, gọi health check rồi gửi nhiều order request song song.

**Gợi ý từ AI**

- Dùng `CookieManager` để giữ session
- Dùng `ExecutorService` và `CompletableFuture`
- Sinh payload ngẫu nhiên theo danh sách variant
- Thống kê success/fail và thời gian phản hồi

**Sinh viên đã làm gì**

- Tách simulator thành Maven JAR độc lập
- Cho phép truyền tham số `--server`, `--orders`, `--concurrency`, `--variants`, `--email`, `--password`, `--voucher`
- Kết nối trực tiếp với API web app đã triển khai

**File liên quan**

- [src/simulator/pom.xml](/c:/Users/triet/lab211/src/simulator/pom.xml)
- [src/simulator/src/main/java/com/lab211/simulator/SimulatorMain.java](/c:/Users/triet/lab211/src/simulator/src/main/java/com/lab211/simulator/SimulatorMain.java)

### Log 11 - Hoàn thiện review chỉ cho người đã mua

**Vấn đề**

Cần tránh review ảo và đảm bảo logic phù hợp nghiệp vụ mua hàng.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI gợi ý cách kiểm tra người dùng đã từng mua sản phẩm trước khi cho review.

**Gợi ý từ AI**

- Truy vấn `orders`, `order_items`, `product_variants` để kiểm tra lịch sử mua
- Nếu đã có review thì update, chưa có thì create

**Sinh viên đã làm gì**

- Áp dụng kiểm tra `hasPurchasedProduct`
- Chỉ cho phép 1 review/user/product nhờ unique constraint và logic update

**File liên quan**

- [src/core_app/src/main/java/com/lab211/ecommerce/servlet/ReviewServlet.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/servlet/ReviewServlet.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/dao/ReviewDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/ReviewDAO.java)
- [src/core_app/src/main/java/com/lab211/ecommerce/dao/OrderDAO.java](/c:/Users/triet/lab211/src/core_app/src/main/java/com/lab211/ecommerce/dao/OrderDAO.java)

### Log 12 - Chỉnh giao diện theo mẫu tham khảo

**Vấn đề**

Người dùng muốn giao diện gần giống một project React tham khảo: màu cam trắng, hero banner, flash-sale strip, card sản phẩm, cart layout và modal chọn sản phẩm.

**Prompt/Tóm tắt yêu cầu với AI**

Nhờ AI đọc dự án giao diện tham khảo và chuyển tinh thần thiết kế đó sang JSP/CSS mà không đổi backend.

**Gợi ý từ AI**

- Tái cấu trúc `navbar.jspf`, `products.jsp`, `cart.jsp`, `checkout.jsp`
- Tạo theme màu cam, badge, card, hero, grid
- Đưa quick view/modal vào danh sách sản phẩm

**Sinh viên đã làm gì**

- Điều chỉnh lại để phù hợp cấu trúc JSP hiện có
- Giữ dữ liệu thật từ MySQL thay vì mock data
- Loại bỏ những phần header không cần thiết theo feedback

**File liên quan**

- [src/core_app/src/main/webapp/WEB-INF/views/navbar.jspf](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/navbar.jspf)
- [src/core_app/src/main/webapp/WEB-INF/views/products.jsp](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/products.jsp)
- [src/core_app/src/main/webapp/WEB-INF/views/cart.jsp](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/cart.jsp)
- [src/core_app/src/main/webapp/WEB-INF/views/checkout.jsp](/c:/Users/triet/lab211/src/core_app/src/main/webapp/WEB-INF/views/checkout.jsp)
- [src/core_app/src/main/webapp/assets/css/app.css](/c:/Users/triet/lab211/src/core_app/src/main/webapp/assets/css/app.css)

## 5. Những điểm nhóm đã tự kiểm tra/chỉnh lại sau khi nhận gợi ý AI

- Không giữ nguyên log cũ dạng 1 đoạn tóm tắt, mà bổ sung lại theo từng nhóm việc
- Không dùng dữ liệu mock cho web app chính, dữ liệu hiển thị lấy từ MySQL qua DAO
- Không để voucher là phần mô tả mơ hồ, mà chốt rõ bằng schema và logic `product_id` nullable
- Không để review tự do, mà thêm điều kiện phải mua hàng mới được đánh giá
- Không hiển thị toàn bộ catalog ở trang đầu, mà kết hợp featured products và search
- Không chỉ tạo order đơn giản, mà thêm transaction, validate stock và retry khi tranh chấp

## 6. Kết luận

AI đã hỗ trợ mạnh ở phần gợi ý cấu trúc, SQL, logic servlet/JSP, dữ liệu mẫu và chỉnh giao diện. Tuy nhiên sản phẩm cuối cùng là kết quả của việc đọc lại, sửa lại và tích hợp thủ công vào repo hiện tại. Nhật ký này được cập nhật để phản ánh trung thực hơn phạm vi AI đã tham gia trong project.
