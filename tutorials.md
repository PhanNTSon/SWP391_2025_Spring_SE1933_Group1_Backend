# Hướng Dẫn Cài Đặt và Chạy Dự Án Steam Clone Backend

Tài liệu này cung cấp hướng dẫn chi tiết về các yêu cầu môi trường, cách cấu hình biến môi trường (`.env` / `application.properties`), và các lệnh để khởi chạy dự án trong các môi trường khác nhau (Local Mock vs Production).

## 1. Yêu cầu hệ thống (Requirements)
Để có thể chạy được dự án này, máy tính của bạn cần được cài đặt các phần mềm sau:
- **Java Development Kit (JDK)**: Phiên bản 21 (hoặc tương thích).
- **Maven**: Phiên bản 3.8 trở lên (dùng để quản lý dependency và build).
- **Cơ sở dữ liệu PostgreSQL**: Phiên bản 17.x trở lên.
- **Git** (tuỳ chọn nhưng khuyên dùng).

---

## 2. Các biến môi trường cần thiết (Environment Variables)

Dự án sử dụng nhiều dịch vụ bên thứ 3 (Cloudinary, Cloudflare R2, Google, Youtube, Gemini). Dưới đây là danh sách đầy đủ các biến môi trường:

### Database & Security
- `SPRING_DATASOURCE_URL`: URL kết nối database, ví dụ: `jdbc:postgresql://localhost:5432/steam_clone_db`
- `SPRING_DATASOURCE_USERNAME`: Tên người dùng database (VD: `postgres`)
- `SPRING_DATASOURCE_PASSWORD`: Mật khẩu database
- `JWT_SECRET`: Chuỗi khóa bí mật dùng để mã hóa và giải mã JWT token (cần một chuỗi ngẫu nhiên đủ dài và bảo mật)
- `JWT_EXPIRATION_MS`: Thời gian sống của JWT token tính bằng milliseconds (VD: `3600000` = 1 tiếng)

### Google OAuth2
- `GOOGLE_CLIENT_ID`: Client ID lấy từ Google Cloud Console
- `GOOGLE_CLIENT_SECRET`: Client Secret lấy từ Google Cloud Console

### Cloudinary (Dịch vụ quản lý hình ảnh / avatar)
- `CLOUDINARY_CLOUD_NAME`: Tên cloud của bạn trên Cloudinary
- `CLOUDINARY_API_KEY`: Khóa API của Cloudinary
- `CLOUDINARY_API_SECRET`: Secret API của Cloudinary

### Cloudflare R2 (Lưu trữ game file)
- `R2_ENDPOINT`: Endpoint URL của R2 (Ví dụ: `https://<account_id>.r2.cloudflarestorage.com`)
- `R2_BUCKET_NAME`: Tên bucket trên R2
- `R2_ACCESS_KEY`: R2 Access Key ID
- `R2_SECRET_KEY`: R2 Secret Access Key

### Các API khác
- `GEMINI_KEY`: Khóa API của Google Gemini AI
- `YOUTUBE_API_KEY`: Khóa API của Youtube Data API

---

## 3. Chạy dự án: Các trường hợp sử dụng (Use Cases)

Dự án đã được thiết kế kiến trúc thông minh để có thể hoạt động mà không bắt buộc bạn phải có đủ mọi API Key ngay trong quá trình phát triển (Local Mock).

### Trường hợp 1: Chạy Local (Mock Mode) - Dành cho phát triển

Khi bạn chỉ muốn chạy backend trên máy tính cá nhân để phát triển tính năng, kiểm thử luồng hoạt động mà không muốn bị vướng mắc vào các dịch vụ bên ngoài, hãy cấu hình các biến môi trường thành chữ **`mock`** (hoặc để chuỗi rỗng đối với các API không kiểm tra null).

**Ví dụ thiết lập biến môi trường (trong Windows Environment Variables, IntelliJ IDEA, hoặc `.env`):**
```properties
# Database thực tế của bạn
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/steam_clone_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET=this_is_a_mock_secret_key_for_local_testing_only_please_make_it_long
JWT_EXPIRATION_MS=3600000

# Google OAuth2 (Mock)
GOOGLE_CLIENT_ID=mock
GOOGLE_CLIENT_SECRET=mock

# Cloudinary (Mock)
CLOUDINARY_CLOUD_NAME=mock
CLOUDINARY_API_KEY=mock
CLOUDINARY_API_SECRET=mock

# R2 Storage (Mock)
R2_ENDPOINT=mock
R2_BUCKET_NAME=mock
R2_ACCESS_KEY=mock
R2_SECRET_KEY=mock

# AI & Youtube (Mock)
GEMINI_KEY=mock
YOUTUBE_API_KEY=mock
```

**Kết quả:** Hệ thống sẽ tự động nhận diện giá trị `mock` và sử dụng các "Mock Service". Ví dụ, khi upload file lên R2, thay vì tải lên Cloudflare thực tế, hệ thống sẽ lưu file tạm vào folder `temp/` trong server để dự án vẫn tiếp tục chạy không báo lỗi.

---

### Trường hợp 2: Chạy Deploy / Production (Live Mode)

Khi deploy server lên môi trường thật (Ví dụ: Render, AWS, Heroku, VPS...), bạn **BẮT BUỘC** phải cung cấp đầy đủ các giá trị chuỗi thực tế cho tất cả các dịch vụ.

1. **Database:** Cung cấp URL, username, password từ nhà cung cấp DB Cloud (AWS RDS, Supabase, Neon...).
2. **Flyway Migration:** Dự án tự động thực thi schema (file `V1__Create_Schema.sql`). Để tránh mất dữ liệu, `spring.flyway.clean-disabled` đã được thiết lập `false` trong code. Nhưng khi deploy, bạn KHÔNG ĐƯỢC chạy lệnh Clean.
3. **API Keys:** Cung cấp Cloudinary, R2, Gemini, Youtube, Google OAuth keys hoàn chỉnh thông qua giao diện cấu hình Environment Variables của nền tảng Deploy.

**Lưu ý khi deploy:** KHÔNG sử dụng chuỗi `"mock"` trong môi trường production vì các file tải lên sẽ bị lỗi hoặc bị mất khi server khởi động lại.

---

## 4. Các câu lệnh thao tác dự án (Commands)

### Biên dịch dự án (Build)
Để biên dịch và đóng gói toàn bộ code thành file `.jar`:
```bash
mvn clean install -DskipTests
```
*(Cờ `-DskipTests` giúp bỏ qua kiểm tra tự động nếu bạn chưa setup DB test)*

### Chạy dự án (Run Local)
Khởi chạy ứng dụng Spring Boot bằng lệnh:
```bash
mvn spring-boot:run
```
*(Lệnh này cũng có thể sử dụng cùng các biến môi trường trực tiếp trên terminal nếu cần thiết)*

### Đóng gói và Chạy bằng Docker

Dự án được tích hợp sẵn `Dockerfile` đa tầng và file `docker-compose.yml` để bạn có thể chạy đồng thời cả **Backend** và cơ sở dữ liệu **PostgreSQL** trong môi trường Container.

#### Cách 1: Chạy cả Backend & Database bằng Docker Compose (Khuyên dùng)
Bạn không cần cài đặt PostgreSQL hay Java trên máy cá nhân, Docker Compose sẽ tự động thiết lập và liên kết chúng.

1. **Khởi chạy hệ thống:**
   Đảm bảo bạn đang ở thư mục gốc của backend (nơi chứa file `docker-compose.yml` và `.env`), chạy lệnh sau:
   ```bash
   docker compose up -d --build
   ```
   *Lệnh này sẽ build image của backend từ mã nguồn và kéo PostgreSQL container về chạy ngầm.*

2. **Dừng hệ thống:**
   ```bash
   docker compose down
   ```

*(Lưu ý: Docker Compose đã tự động ghi đè URL database thành `jdbc:postgresql://db:5432/steam_clone_db` để backend kết nối trực tiếp đến container Database `db` cùng mạng. Do đó bạn không cần thay đổi file `.env` khi sử dụng cách này).*

---

#### Cách 2: Chỉ chạy Backend bằng Docker Container (Dùng DB trên Host OS)
Nếu bạn muốn sử dụng Database PostgreSQL cài trực tiếp trên máy thật (Host OS) và chỉ muốn chạy Backend trên Docker:

1. **Build Docker Image cho Backend:**
   ```bash
   docker build -t centurion_backend:latest .
   ```

2. **Khởi chạy container với file `.env`:**
   ```bash
   docker run -p 8080:8080 --env-file .env centurion_backend:latest
   ```

   **Lưu ý quan trọng về kết nối Database:**
   Vì ứng dụng chạy bên trong container, `localhost` sẽ trỏ tới chính container chứ không phải máy chủ của bạn. Bạn cần mở file `.env` và sửa URL kết nối database thành:
   ```properties
   SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/steam_clone_db
   ```
   *(Giá trị `host.docker.internal` giúp Docker Container hiểu và kết nối được với PostgreSQL đang chạy trên máy thật).*

### Xóa dữ liệu Database (Clean & Reset)
Nếu bạn thay đổi file `V1__Create_Schema.sql` hoặc làm hỏng dữ liệu và muốn reset trắng lại database:
1. Đảm bảo cấu hình `spring.flyway.clean-disabled=false`
2. Chạy ứng dụng một lần hoặc sử dụng plugin Flyway.
*(Ứng dụng hiện tại đã được cấu hình bean `FlywayCleanConfig` sẽ tự động dọn dẹp và tạo lại schema mỗi khi startup để hỗ trợ quá trình phát triển schema. Ghi chú: Nhớ gỡ bỏ logic này trong `FlywayCleanConfig.java` trước khi deploy ra Production).*

---

## 5. Tài Liệu Hướng Dẫn Tích Hợp OpenAPI / Swagger UI

Dự án đã tích hợp thư viện **Springdoc OpenAPI Starter** để tự động tạo tài liệu API tương tác trực quan.

### 5.1. Cách Truy Cập Tài Liệu API
Khi ứng dụng backend đang chạy (ví dụ trên cổng `8080`), bạn có thể truy cập bằng trình duyệt theo các địa chỉ sau:
- **Giao diện Swagger UI (Khuyên dùng)**: `http://localhost:8080/swagger-ui/index.html`
  - *Tại đây, bạn có thể xem chi tiết danh sách Controller, tham số API, kiểu dữ liệu gửi lên/trả về và chạy thử trực tiếp (Try it out).*
- **Định dạng OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
  - *Được sử dụng cho các công cụ Client Code Generator hoặc import trực tiếp vào Postman.*

### 5.2. Hướng Dẫn Cấu Hình Spring Security
Mặc định, Spring Security sẽ chặn các đường dẫn Swagger. Hãy đảm bảo bạn cấu hình cho phép truy cập công khai trong lớp cấu hình Security (ví dụ `SecurityConfig.java` hoặc tương đương):

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            // Cho phép truy cập công khai các đường dẫn Swagger UI và OpenAPI Docs
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

### 5.3. Các Annotation Hữu Ích Để Viết API Docs
Để viết tài liệu rõ ràng, hãy thêm các annotation của `io.swagger.v3.oas.annotations` vào Controller của bạn:
- `@Tag(name = "Tên Nhóm API", description = "Mô tả nhóm API")`: Đặt ở cấp Class Controller.
- `@Operation(summary = "Mô tả ngắn", description = "Mô tả chi tiết")`: Đặt ở cấp phương thức (API Method).
- `@ApiResponse(responseCode = "200", description = "Thành công")`: Định nghĩa kiểu dữ liệu trả về cho client.

*Ví dụ:*
```java
@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Controller", description = "Quản lý danh sách game của Steam Clone")
public class GameController {

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết game", description = "Trả về thông tin chi tiết của một trò chơi theo ID")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        // ...
    }
}
```

