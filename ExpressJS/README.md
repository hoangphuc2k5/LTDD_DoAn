# Tegram Backend

ExpressJS + MongoDB backend used by the Android app.

## Cấu trúc thư mục

- `src/server.js`: entry point, khởi động app và kết nối MongoDB
- `src/app.js`: cấu hình Express, middleware, route chính
- `src/config/db.js`: kết nối MongoDB
- `src/controllers/`: xử lý logic `auth` và `users`
- `src/routes/`: khai báo route `/auth` và `/users`
- `src/models/`: schema Mongoose
- `src/middleware/`: xử lý lỗi và 404
- `src/utils/`: helper dùng chung

## Chạy local

1. Copy `.env.example` thành `.env`
2. Cập nhật `MONGODB_URI` và `JWT_SECRET`
3. Chạy `npm install`
4. Chạy `npm run dev`

Backend mặc định chạy ở port `3001`.

## API chính

- `GET /health`
- `POST /auth/register`
- `POST /auth/login`
- `POST /users/sync`

## Ghi chú

- App Android dùng base URL `http://10.0.2.2:3001/` khi chạy trên emulator.
- Nếu backend dừng, app sẽ báo lỗi kết nối và không đăng nhập được vì đã chuyển sang backend-only.