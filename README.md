# Tegram Project

Dự án này gồm 2 phần chính: Android frontend và ExpressJS backend.

## Cấu trúc tổng quan

- `app/`: ứng dụng Android
- `ExpressJS/`: backend ExpressJS + MongoDB
- `gradle/`, `build.gradle.kts`, `settings.gradle.kts`: cấu hình Gradle cho Android project

## Frontend Android

- UI đăng nhập/đăng ký nằm trong `app/src/main/java/com/example/tegram/presentation/auth/`
- Điều hướng chính nằm trong `app/src/main/java/com/example/tegram/presentation/navigation/AppNavGraph.kt`
- Tầng dữ liệu nằm trong `app/src/main/java/com/example/tegram/data/`
- Android đang gọi backend qua Retrofit, base URL trỏ tới `http://10.0.2.2:3001/` khi chạy emulator

## Backend ExpressJS

- Entry point: `ExpressJS/src/server.js`
- Express app: `ExpressJS/src/app.js`
- MongoDB connection: `ExpressJS/src/config/db.js`
- Auth routes: `ExpressJS/src/routes/authRoutes.js`
- User sync routes: `ExpressJS/src/routes/userRoutes.js`

## Chạy dự án

- Backend: vào `ExpressJS/`, copy `.env.example` thành `.env`, chạy `npm install`, rồi `npm run dev`
- Android: mở project trong Android Studio và chạy app trên emulator

## Lưu ý

- Dữ liệu người dùng hiện phụ thuộc backend ExpressJS 100%
- Nếu backend không chạy, màn hình login sẽ báo lỗi kết nối rõ ràng
