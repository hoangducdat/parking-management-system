# TÀI LIỆU DỰ ÁN: QUẢN LÝ NHÀ XE

## 1. Giới thiệu về dự án
Dự án "Quản lý nhà xe" là một hệ thống phần mềm giúp quản lý việc gửi và lấy xe tại một bãi đỗ xe. Mục tiêu là tự động hóa các công việc như ghi nhận xe vào/ra, tính phí gửi xe, theo dõi số lượng chỗ trống, và cung cấp thống kê cho người quản lý. Hệ thống này phù hợp cho các bãi đỗ xe nhỏ hoặc vừa, chẳng hạn như ở chung cư, siêu thị, hoặc trường học.

Dự án được xây dựng để:
- Giúp nhân viên dễ dàng kiểm soát xe vào và ra.
- Tính phí gửi xe một cách chính xác, không cần tính toán thủ công.
- Cung cấp thông tin nhanh chóng cho người quản lý (ví dụ: doanh thu, số xe đã gửi).

---

## 2. Dự án làm được những gì?
Hệ thống có các chức năng chính sau:

### 2.1. Ghi nhận xe vào bãi (Check-in)
- Khi một xe (xe máy hoặc ô tô) vào bãi, nhân viên nhập biển số xe và loại xe (xe máy hoặc ô tô).
- Hệ thống sẽ tự động chọn một chỗ trống trong bãi và ghi lại thời gian xe vào.

### 2.2. Ghi nhận xe ra bãi và tính phí (Check-out)
- Khi xe rời bãi, nhân viên nhập biển số xe.
- Hệ thống tính phí dựa trên loại xe và thời gian gửi:
  - **Xe máy:** 5.000 VNĐ/lần hoặc 10.000 VNĐ nếu qua đêm.
  - **Ô tô:** 10.000 VNĐ/lần hoặc 20.000 VNĐ nếu qua đêm.
- "Qua đêm" được hiểu là xe vào trước 6 giờ tối và ra sau 6 giờ sáng ngày hôm sau.

### 2.3. Tìm kiếm xe trong bãi
- Nhân viên có thể nhập biển số xe để kiểm tra xem xe đang ở đâu trong bãi (vị trí nào) và đã vào từ lúc nào.

### 2.4. Kiểm tra số chỗ trống
- Hệ thống cho biết còn bao nhiêu chỗ trống trong bãi để nhân viên quyết định có nhận thêm xe hay không.

### 2.5. Thống kê cho quản lý
- Cung cấp các thông tin như:
  - Số lượng xe đã gửi trong một khoảng thời gian (ví dụ: trong ngày).
  - Tổng doanh thu từ phí gửi xe.
  - Thời gian trung bình mỗi xe gửi trong bãi.

### 2.6. Quản lý người dùng
- Chỉ có quản lý (admin) mới có thể thêm nhân viên mới vào hệ thống.
- Nhân viên (staff) chỉ được phép thực hiện các thao tác như check-in, check-out, và tra cứu.

---

## 3. Cách hệ thống hoạt động
Hệ thống sử dụng công nghệ để lưu trữ và xử lý thông tin:
- **Cơ sở dữ liệu (Database):** Lưu thông tin về xe, nhân viên, và các lần gửi xe. Ở đây, chúng tôi dùng H2 (một loại cơ sở dữ liệu nhẹ để thử nghiệm).
- **Redis:** Một công cụ giúp kiểm tra nhanh xem xe đã vào bãi hay chưa, tránh trường hợp ghi nhầm.
- **Liquibase:** Giúp tạo các bảng dữ liệu tự động (như bảng xe, bảng chỗ đỗ) mà không cần người quản lý phải thiết lập thủ công.

### Ví dụ thực tế:
1. Xe máy biển số "29A-12345" vào bãi lúc 10:00 sáng ngày 31/03/2025:
   - Nhân viên nhập thông tin, hệ thống ghi nhận và chọn chỗ trống (ví dụ: khu A).
2. Xe ra lúc 3:00 chiều cùng ngày:
   - Nhân viên nhập biển số, hệ thống tính phí 5.000 VNĐ (vì không qua đêm) và trả lại chỗ trống.
3. Nếu xe ra lúc 7:00 sáng ngày 01/04/2025 (qua đêm):
   - Phí sẽ là 10.000 VNĐ.

---

## 4. Ai có thể sử dụng?
- **Nhân viên bãi xe (Staff):** Thực hiện check-in, check-out, tra cứu xe, và xem số chỗ trống.
- **Quản lý (Admin):** Ngoài các chức năng của nhân viên, còn có thể thêm nhân viên mới và xem thống kê.

---

## 5. Các bước triển khai hệ thống
Để sử dụng hệ thống, cần làm theo các bước sau:

### 5.1. Chuẩn bị môi trường
- **Máy tính:** Cần một máy tính có kết nối mạng để chạy phần mềm.
- **Docker:** Cài đặt Docker để chạy Redis (một công cụ hỗ trợ kiểm tra nhanh thông tin xe).
- **Java:** Cài đặt Java để chạy chương trình chính.

### 5.2. Chạy Redis
- Redis là một phần mềm nhỏ giúp hệ thống hoạt động nhanh hơn. Để chạy:
  1. Tạo file `docker-compose.yml` (đã cung cấp trong dự án).
  2. Mở terminal, vào thư mục chứa file, gõ lệnh:
     ```
     docker-compose up -d
     ```
  3. Redis sẽ chạy ở địa chỉ `localhost:6379`.

### 5.3. Chạy chương trình
- Dùng lệnh sau trong terminal:
  ```
  mvn spring-boot:run
  ```
- Hệ thống sẽ tự động tạo các bảng dữ liệu và sẵn sàng hoạt động.

### 5.4. Sử dụng qua API
- Nhân viên hoặc quản lý sẽ sử dụng các lệnh (gọi là API) để tương tác với hệ thống. Ví dụ:
  - Check-in: Gửi thông tin xe qua lệnh `POST /api/parking/check-in`.
  - Check-out: Gửi biển số xe qua lệnh `POST /api/parking/check-out?licensePlate=29A-12345`.
- Thông thường, cần một giao diện đơn giản (như ứng dụng web hoặc mobile) để nhân viên dễ thao tác hơn, nhưng hiện tại hệ thống chỉ có API.

---

## 6. Các tính năng kỹ thuật (dành cho người hiểu công nghệ)
Dành cho những ai muốn biết sâu hơn (có thể bỏ qua nếu không quan tâm):
- **Ngôn ngữ lập trình:** Java, dùng Spring Boot để xây dựng.
- **Cơ sở dữ liệu:** H2 (dễ thay bằng MySQL hoặc PostgreSQL nếu cần).
- **Quản lý schema:** Liquibase tự động tạo bảng với ID dạng UUID (chuỗi duy nhất).
- **Bảo mật:** Có phân quyền (admin/staff) và mã hóa mật khẩu.
- **Redis:** Lưu thông tin xe đang trong bãi để kiểm tra nhanh.

---

## 7. Kết quả mong đợi
- **Nhân viên:** Dễ dàng quản lý xe, không cần ghi chép thủ công.
- **Quản lý:** Biết được doanh thu, số xe gửi, và tình trạng bãi xe bất cứ lúc nào.
- **Khách hàng:** Gửi xe nhanh chóng, phí minh bạch.

---

## 8. Ví dụ sử dụng thực tế
### Trường hợp 1: Gửi xe máy trong ngày
- **Check-in:** Xe máy "29A-12345" vào lúc 10:00 ngày 31/03/2025.
- **Check-out:** Ra lúc 15:00 cùng ngày.
- **Kết quả:** Phí 5.000 VNĐ.

### Trường hợp 2: Gửi ô tô qua đêm
- **Check-in:** Ô tô "30F-56789" vào lúc 17:00 ngày 31/03/2025.
- **Check-out:** Ra lúc 07:00 ngày 01/04/2025.
- **Kết quả:** Phí 20.000 VNĐ (qua đêm).

### Trường hợp 3: Thống kê
- Quản lý muốn xem doanh thu ngày 31/03/2025:
  - Gửi lệnh `GET /api/parking/stats?start=2025-03-31T00:00:00&end=2025-03-31T23:59:59`.
  - Kết quả: "Doanh thu: 50.000 VNĐ, số xe: 10".

---

## 9. Kết luận
Dự án "Quản lý nhà xe" là một giải pháp đơn giản nhưng hiệu quả để thay thế cách quản lý thủ công. Dù hiện tại chỉ có các lệnh API, nó vẫn đáp ứng tốt nhu cầu cơ bản và có thể mở rộng thêm (ví dụ: thêm giao diện, hỗ trợ thanh toán online). Nếu cần cải tiến, chỉ cần yêu cầu người phát triển bổ sung theo nhu cầu thực tế.
