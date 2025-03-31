# TÀI LIỆU DỰ ÁN: QUẢN LÝ NHÀ XE

## 1. Giới thiệu về dự án
Dự án "Quản lý nhà xe" là một hệ thống phần mềm giúp quản lý việc gửi và lấy xe tại một bãi đỗ xe. Mục tiêu là tự động hóa các công việc như ghi nhận xe vào/ra, tính phí gửi xe, theo dõi số lượng chỗ trống trong từng khu vực, và cung cấp thống kê cho người quản lý. Hệ thống này phù hợp cho các bãi đỗ xe có nhiều khu vực (ví dụ: khu A, khu B) như ở chung cư, siêu thị, hoặc trường học.

Dự án được xây dựng để:
- Giúp nhân viên dễ dàng kiểm soát xe vào/ra và biết xe đang ở khu vực nào.
- Tính phí gửi xe chính xác dựa trên loại xe và thời gian gửi (thường hoặc qua đêm).
- Cung cấp thông tin chi tiết cho quản lý, như doanh thu, số xe đã gửi, và tình trạng chỗ đỗ theo từng khu vực.

---

## 2. Dự án làm được những gì?
Hệ thống có các chức năng chính sau:

### 2.1. Ghi nhận xe vào bãi (Check-in)
- Khi xe (xe máy hoặc ô tô) vào bãi, nhân viên nhập biển số xe và loại xe.
- Hệ thống tự động chọn một khu vực (ví dụ: khu A) và một chỗ trống trong khu vực đó để ghi nhận xe.
- Thông tin như thời gian vào, khu vực đỗ sẽ được lưu lại.

### 2.2. Ghi nhận xe ra bãi và tính phí (Check-out)
- Khi xe rời bãi, nhân viên nhập biển số xe và tên người xử lý thanh toán (thường là nhân viên).
- Hệ thống tính phí dựa trên loại xe và thời gian gửi:
  - **Xe máy:** 5.000 VNĐ/lần hoặc 10.000 VNĐ nếu qua đêm.
  - **Ô tô:** 10.000 VNĐ/lần hoặc 20.000 VNĐ nếu qua đêm.
- "Qua đêm" được hiểu là xe vào trước 6 giờ tối và ra sau 6 giờ sáng ngày hôm sau.
- Thông tin thanh toán (số tiền, thời gian, người xử lý) được lưu riêng để dễ theo dõi.

### 2.3. Tìm kiếm xe trong bãi
- Nhân viên nhập biển số xe để biết xe đang ở khu vực nào, vào từ lúc nào, và chưa ra hay đã ra.

### 2.4. Kiểm tra số chỗ trống
- Hệ thống cho biết còn bao nhiêu chỗ trống trong từng khu vực (ví dụ: khu A còn 5 chỗ, khu B còn 3 chỗ) để nhân viên quyết định nhận thêm xe.

### 2.5. Thống kê cho quản lý
- Cung cấp thông tin như:
  - Số lượng xe đã gửi trong một khoảng thời gian (ví dụ: trong ngày).
  - Tổng doanh thu từ phí gửi xe.
  - Thời gian trung bình mỗi xe gửi trong bãi.

### 2.6. Quản lý người dùng
- Chỉ quản lý (admin) mới có thể thêm nhân viên mới vào hệ thống.
- Nhân viên (staff) chỉ được phép thực hiện các thao tác như check-in, check-out, và tra cứu.

---

## 3. Cách hệ thống hoạt động
Hệ thống sử dụng các "kho dữ liệu" (gọi là bảng) để lưu trữ và xử lý thông tin:
- **Kho "Khu vực đỗ xe" (`parking_zones`):** Lưu thông tin về các khu vực (ví dụ: khu A, khu B) và số chỗ tối đa mỗi khu vực.
- **Kho "Lịch sử gửi xe" (`parking_records`):** Lưu thông tin mỗi lần xe vào/ra, bao gồm biển số, loại xe, khu vực, thời gian.
- **Kho "Chỗ đỗ xe" (`parking_slots`):** Theo dõi từng chỗ đỗ trong mỗi khu vực (trống hay đã có xe).
- **Kho "Nhật ký thanh toán" (`payment_logs`):** Lưu chi tiết các lần thanh toán (số tiền, thời gian, ai xử lý).
- **Kho "Người dùng" (`users`):** Lưu thông tin nhân viên và quản lý (tên đăng nhập, mật khẩu, vai trò).

Ngoài ra:
- **Redis:** Công cụ giúp kiểm tra nhanh xem xe đã vào bãi hay chưa.
- **Liquibase:** Tự động tạo và quản lý các kho dữ liệu khi hệ thống khởi động.

### Ví dụ thực tế:
1. Xe máy "29A-12345" vào bãi lúc 10:00 sáng ngày 31/03/2025:
   - Nhân viên nhập thông tin, hệ thống chọn khu A (còn chỗ trống) và ghi nhận.
2. Xe ra lúc 15:00 cùng ngày:
   - Nhân viên nhập biển số, hệ thống tính phí 5.000 VNĐ (không qua đêm), lưu thanh toán, và giải phóng chỗ trống ở khu A.
3. Nếu xe ra lúc 07:00 ngày 01/04/2025 (qua đêm):
   - Phí là 10.000 VNĐ, thông tin thanh toán được ghi lại.

---

## 4. Ai có thể sử dụng?
- **Nhân viên bãi xe (Staff):** Thực hiện check-in, check-out, tra cứu xe, và xem số chỗ trống theo khu vực.
- **Quản lý (Admin):** Ngoài các chức năng của nhân viên, còn có thể thêm nhân viên mới và xem thống kê.

---

## 5. Các bước triển khai hệ thống
Để sử dụng hệ thống, cần làm theo các bước sau:

### 5.1. Chuẩn bị môi trường
- **Máy tính:** Cần một máy tính có kết nối mạng để chạy phần mềm.
- **Docker:** Cài đặt Docker để chạy Redis (công cụ hỗ trợ kiểm tra nhanh).
- **Java:** Cài đặt Java để chạy chương trình chính.

### 5.2. Chạy Redis
- Redis giúp hệ thống hoạt động nhanh hơn. Cách chạy:
  1. Tạo file `docker-compose.yml` (đã cung cấp trong dự án).
  2. Mở terminal, vào thư mục chứa file, gõ lệnh:
     ```
     docker-compose up -d
     ```
  3. Redis sẽ chạy ở địa chỉ `localhost:6379`.

### 5.3. Chuẩn bị dữ liệu ban đầu
- Trước khi chạy, cần thêm thông tin khu vực (ví dụ: khu A với 10 chỗ, khu B với 5 chỗ) vào kho `parking_zones` và tạo các chỗ đỗ tương ứng trong `parking_slots`. Điều này có thể làm thủ công qua giao diện H2 hoặc thêm vào Liquibase.

### 5.4. Chạy chương trình
- Dùng lệnh trong terminal:
  ```
  mvn spring-boot:run
  ```
- Hệ thống sẽ tự động tạo các kho dữ liệu và sẵn sàng hoạt động.

### 5.5. Sử dụng qua API
- Nhân viên hoặc quản lý dùng các lệnh (API) để tương tác với hệ thống. Ví dụ:
  - Check-in: Gửi thông tin xe qua `POST /api/parking/check-in`.
  - Check-out: Gửi biển số và người xử lý qua `POST /api/parking/check-out?licensePlate=29A-12345&paidBy=staff1`.
  - Xem chỗ trống: `GET /api/parking/slots/available?zoneId=UUID_của_khu_A`.
- Trong tương lai, có thể làm giao diện đơn giản (web hoặc mobile) để dễ thao tác hơn.

---

## 6. Các tính năng kỹ thuật (dành cho người hiểu công nghệ)
Dành cho những ai muốn biết sâu hơn (có thể bỏ qua nếu không quan tâm):
- **Ngôn ngữ lập trình:** Java, dùng Spring Boot.
- **Cơ sở dữ liệu:** H2 (dễ thay bằng MySQL hoặc PostgreSQL).
- **Quản lý kho dữ liệu:** Liquibase tự động tạo bảng với ID dạng UUID (chuỗi duy nhất).
- **Liên kết giữa các bảng:**
  - `parking_zones` liên kết với `parking_records` và `parking_slots` qua `zone_id`.
  - `parking_records` liên kết với `payment_logs` qua `parking_record_id`.
- **Bảo mật:** Phân quyền (admin/staff), mật khẩu mã hóa.
- **Redis:** Lưu thông tin xe đang trong bãi để kiểm tra nhanh.

---

## 7. Kết quả mong đợi
- **Nhân viên:** Dễ dàng quản lý xe, biết xe ở khu vực nào, không cần ghi chép thủ công.
- **Quản lý:** Theo dõi doanh thu, số xe gửi, và tình trạng từng khu vực bất cứ lúc nào.
- **Khách hàng:** Gửi xe nhanh chóng, phí minh bạch, thông tin rõ ràng.

---

## 8. Ví dụ sử dụng thực tế
### Trường hợp 1: Gửi xe máy trong ngày
- **Check-in:** Xe máy "29A-12345" vào lúc 10:00 ngày 31/03/2025, đỗ ở khu A.
- **Check-out:** Ra lúc 15:00 cùng ngày, nhân viên "staff1" xử lý.
- **Kết quả:** Phí 5.000 VNĐ, chỗ trống ở khu A được giải phóng.

### Trường hợp 2: Gửi ô tô qua đêm
- **Check-in:** Ô tô "30F-56789" vào lúc 17:00 ngày 31/03/2025, đỗ ở khu B.
- **Check-out:** Ra lúc 07:00 ngày 01/04/2025, nhân viên "staff1" xử lý.
- **Kết quả:** Phí 20.000 VNĐ (qua đêm), lưu vào nhật ký thanh toán.

### Trường hợp 3: Thống kê
- Quản lý xem doanh thu ngày 31/03/2025:
  - Gửi lệnh `GET /api/parking/stats?start=2025-03-31T00:00:00&end=2025-03-31T23:59:59`.
  - Kết quả: "Doanh thu: 50.000 VNĐ, số xe: 10".

---

## 9. Kết luận
Dự án "Quản lý nhà xe" giờ đây chi tiết hơn với việc quản lý khu vực đỗ xe và chỗ đỗ cụ thể. Hệ thống không chỉ tự động hóa việc gửi xe mà còn giúp theo dõi tình trạng từng khu vực, lưu lịch sử thanh toán riêng biệt, và dễ dàng mở rộng trong tương lai (ví dụ: thêm khu vực mới, hỗ trợ thanh toán online). Nếu cần cải tiến thêm, chỉ cần yêu cầu người phát triển bổ sung theo nhu cầu thực tế.
