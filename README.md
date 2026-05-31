# Bai-tap-lon
BÀI TẬP LỚN NHÓM 10: HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN (ONLINE AUCTION SYSTEM)
Đơn vị: Khoa Công nghệ thông tin - Trường Đại học Công nghệ (VNU-UET)   
Lớp học phần: K701-IT4
Thành viên nhóm và phân công nhiệm vụ
Đào Duy Anh (MSV: 25020004) - Trưởng nhóm: Thiết kế kiến trúc DB quan hệ đám mây (Aiven Cloud MySQL), triển khai toàn bộ gói thực thể com.auction.model, gói truy xuất dữ liệu com.auction.dao, và quản lý tiến độ Git Workflow.
Mai Đức Cường (MSV: 25020049): Thiết lập hạ tầng Socket mạng TCP/IP, cấu hình và đóng gói dữ liệu nhị phân hiệu năng cao với Google Protocol Buffers, quản lý luồng xử lý đồng thời (Concurrency) phía Server.
Vũ Quang Dũng (MSV: 25020081): Hiện thực hóa các Mẫu thiết kế (Design Patterns), xử lý nghiệp vụ lõi phía Server (gói com.auction.manager), quản lý Timer và luồng đếm ngược.
Đậu Đức Dũng (MSV: 25020065): Thiết kế giao diện đồ họa UI/UX (file .fxml), liên kết luồng tương tác ứng dụng (gói com.auction.controller), cấu hình Runtime và Module hệ thống.

1. Mô tả ngắn gọn bài toán và phạm vi hệ thống:
   Bài toán: Xây dựng một nền tảng đấu giá trực tuyến hoạt động theo mô hình Client - Server phi tập trung kết hợp kiến trúc phân lớp (Layered Architecture). Hệ thống cho phép tổ chức các phiên đấu giá thời gian thực với tính an toàn đồng thời cao, tối ưu băng thông truyền tải và đồng bộ trạng thái tức thời giữa các máy trạm.
   Phạm vi hệ thống & Đối tượng sử dụng: Ứng dụng được cô lập trong phạm vi một ứng dụng Desktop (Client-Server), phân quyền người dùng chặt chẽ thành hai nhóm đối tượng:
   Người quản lý (Admin): Giám sát toàn bộ tiến trình, có quyền hủy phiên hoặc chốt phiên đấu giá khi có sự cố.
   Người tham gia đấu giá (User/Bidder):   Đăng ký, đăng nhập tài khoản hệ thống để thiết lập định danh pháp lý.  Xem danh sách các phòng đấu giá đang mở, truy cập trực tiếp vào phòng theo mã phiên.  Thực hiện hành động nâng giá (Bidding) hợp lệ và theo dõi dòng thời gian thực tế của phiên.

2. Công nghệ sử dụng, môi trường chạy và yêu cầu cài đặt:
   Ngôn ngữ lập trình: Java (Môi trường biên dịch Java JDK 25 - v25.0.2).
   Giao diện người dùng (GUI): JavaFX 17 kết hợp ngôn ngữ định dạng FXML.
   Cơ sở dữ liệu: Hệ quản trị cơ sở dữ liệu đám mây Aiven Cloud MySQL (kết nối qua cơ chế JDBC với Driver
   Kiến trúc & Giao tiếp mạng: Hạ tầng mạng Socket TCP/IP liên tục (Persistent Connection) , đóng gói mã hóa dữ liệu qua Google Protocol Buffers (Protobuf v3.21.9) giúp tối ưu tốc độ và tiết kiệm băng thông.
   Mã sạch & Kiểm thử: Tích hợp plugin Checkstyle (Google Java Style Guide) và framework JUnit 5 để kiểm thử tự động nghiệp vụ lõi.
   Yêu cầu cài đặt (Prerequisites):Đã cài đặt JDK 25.  Đã cài đặt Apache Maven (để quản lý thư viện phụ thuộc và vòng đời build).  Đã cấu hình quyền truy cập internet để kết nối đến DB đám mây Aiven MySQL.

3. Cấu trúc thư mục và các gói mã nguồn (Packages):
   Dự án được tổ chức phân rã mã nguồn tại thư mục gốc src/main/java/com/auction thành các gói chuyên biệt theo kiến trúc MVC kết hợp Layered Architecture:
   src/main/java/com/auction/
   ├── auction/      # Lưu trữ logic đặc thù riêng cho quy trình vận hành và kiểm tra tính hợp lệ của phiên thầu.
   ├── controller/   # Tầng điều phối dữ liệu (Controller trong MVC), liên kết sự kiện từ giao diện FXML.
   ├── dao/          # Tầng đóng gói logic truy xuất, thực thi câu lệnh JDBC trực tiếp với Cloud MySQL.
   ├── factory/      # Triển khai Design Pattern: Factory Method để khởi tạo động danh mục tài sản.
   ├── manager/      # Bộ điều khiển trung tâm phía Server, quản lý đếm ngược và trạng thái vòng đời phiên.
   ├── model/        # Chứa các thực thể nghiệp vụ cốt lõi dưới dạng các đối tượng định danh lớp (Model).
   ├── network/      # Hạ tầng xử lý truyền thông mạng Socket TCP, duy trì kết nối đồng thời từ đa máy trạm.
   ├── observer/     # Triển khai Design Pattern: Observer Pattern để phát tin đồng bộ dữ liệu (Broadcast).
   ├── util/         # Chứa các lớp tiện ích dùng chung và lớp quản lý kết nối cơ sở dữ liệu duy nhất (Singleton).
   ├── Launcher.java # Lớp thực thi chính chứa hàm main, kế thừa Application để kích hoạt giao diện JavaFX.
   ├── module-info.java # Khai báo cấu hình hệ thống Module hóa (Java Jigsaw), cấp quyền phản chiếu dữ liệu.
   └── pom.xml       # Tệp cấu hình Maven định nghĩa môi trường build và quản lý Dependencies ngoại vi.


4. Hướng dẫn chạy Server/Client theo thứ tự cụ thể
   BƯỚC 1 - Khởi động Auction
   BƯỚC 2 - Chạy Launcher

5. Danh sách chức năng đã hoàn thành
   Nhóm chức năng cốt lõi (Core Features):
   Áp dụng nghiêm ngặt 4 tính chất OOP: Đóng gói (private thuộc tính + getter/setter), Kế thừa (từ lớp Entity gốc xuống User, Item xuống các subclass tài sản), Đa hình (ghi đè @Override phương thức printInfo()) và Trừu tượng.  Quản lý người dùng & sản phẩm (CRUD): Phân tách giao diện FXML độc lập; toàn bộ thao tác thêm, sửa, xóa, đọc dữ liệu được xử lý tập trung tại tầng dữ liệu thông qua gói com.auction.dao.  Quản lý vòng đời phiên đấu giá: Sử dụng ScheduledExecutorService tại phía Server để đếm ngược ngầm độc lập cho từng phiên, tự động chuyển trạng thái OPEN -> RUNNING -> FINISHED và tự động chốt người chiến thắng.  Xử lý lỗi & Ngoại lệ: Triển khai cấu trúc try-catch toàn diện từ End-to-End, Server bắt toàn bộ các ngoại lệ nghiệp vụ và đóng gói an toàn vào gói tin Response trả về phía Client.
   Nhóm chức năng nâng cao (Advanced Features):
   Triển khai các Mẫu thiết kế nâng cao (Design Patterns): Singleton Pattern: Áp dụng tại DatabaseConnection.java để tối ưu hóa tài nguyên kết nối duy nhất.  Factory Method: Áp dụng tại ItemFactory.java nhằm quản lý khởi tạo động các danh mục tài sản đấu giá (Art, Electronics, Vehicle).  Observer Pattern: Áp dụng tại gói com.auction.observer để phát tín hiệu đồng bộ giá thầu theo thời gian thực tới tất cả Client kết nối.  
   Xử lý đồng thời an toàn (Concurrency): Sử dụng từ khóa chặn đồng bộ synchronized trên hàm đặt giá placeBid() của AuctionSession, kết hợp cơ chế khóa ReentrantLock tại AuctionManager và cấu trúc dữ liệu an toàn luồng CopyOnWriteArrayList cho danh sách Observer nhằm triệt tiêu hiện tượng Race Condition.
   Đồng bộ dữ liệu thời gian thực (Real-time Update): Server duy trì danh sách ClientHandler đại diện cho từng máy trạm để Broadcast dữ liệu mảng byte (Protobuf). Phía Client nhận dữ liệu và bắt buộc gọi Platform.runLater() để cập nhật luồng đồ họa JavaFX UI một cách an toàn.
   Hệ thống CI/CD tự động hóa qua GitHub Actions: Thiết lập luồng Workflow tự động kích hoạt khi push hoặc pull request lên nhánh main. Hệ thống tự động thực thi chuỗi lệnh mvn compile > mvn test và sẽ từ chối lệnh gộp nhánh (Merge) nếu bất kỳ kịch bản kiểm thử tự động JUnit 5 nào thất bại.

6. Link báo cáo PDF và video demo
   Link video demo: https://drive.google.com/drive/folders/15D_1E56A1BTb2p0Ruv9shAI1H4kj2jy5?fbclid=IwY2xjawSJQz1leHRuA2FlbQIxMABicmlkETFGS2luR3dwVFZHeXZPQ1J0c3J0YwZhcHBfaWQQMjIyMDM5MTc4ODIwMDg5MgABHnU9RMvYx_mWWWFiiRaTndf5MAywO7dYCtIpUMl5JFWOhJIAHQxTS_Grpj_n_aem_dLgL5Uduf9nJVSBzJdnlxQ
   Link báo cáo PDF: https://drive.google.com/file/d/13SScBUvlOtyveMxyns2WcHWcCB58sf_n/view?usp=drive_link
   BTL - Google Drive
