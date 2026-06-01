# TÀI LIỆU THIẾT KẾ CHỨC NĂNG

# MODULE QUẢN LÝ VOCABULARY

# 1. Giới thiệu

Module Quản lý Vocabulary là chức năng cho phép người dùng:

* Quản lý vocabulary cá nhân
* Thêm / sửa / xóa vocabulary
* Tìm kiếm vocabulary
* Import CSV
* Export CSV
* Xem vocabulary công khai từ Internet
* Đồng bộ dữ liệu với MongoDB thông qua ExpressJS API

Hệ thống được xây dựng theo mô hình Client – Server:

* Frontend: Android Studio (Kotlin)
* Backend: ExpressJS (NodeJS)
* Database: MongoDB
* API giao tiếp: REST API

---

# 2. Kiến trúc hệ thống

# 2.1 Frontend Android Studio

Chức năng:

* Hiển thị giao diện
* Gọi API backend bằng Retrofit
* Hiển thị danh sách vocabulary
* Import / Export CSV
* Tìm kiếm vocabulary
* Quản lý dữ liệu local

Công nghệ:

* Kotlin
* RecyclerView
* Retrofit
* MVVM
* Material Design

---

# 2.2 Backend ExpressJS

Chức năng:

* Xử lý REST API
* Kết nối MongoDB
* CRUD vocabulary
* Xử lý tìm kiếm
* Xử lý import/export dữ liệu
* Kết nối API công khai

Công nghệ:

* NodeJS
* ExpressJS
* Mongoose
* JWT Authentication

---

# 2.3 Database MongoDB

MongoDB dùng để lưu:

* User
* Personal Vocabulary
* Public Vocabulary
* Chủ đề vocabulary

---

# 3. Thiết kế giao diện

# 3.1 Màn hình Home Vocabulary

Trang chủ gồm 2 phần chính:

## A. Personal Vocabulary

Hiển thị vocabulary cá nhân của người dùng.

### Chức năng

* Xem danh sách vocabulary
* Thêm vocabulary
* Sửa vocabulary
* Xóa vocabulary
* Import CSV
* Export CSV
* Tìm kiếm vocabulary

### Hiển thị

RecyclerView dạng Card:

* Word
* Meaning
* Topic
* Updated Date

Ví dụ:

apple
Táo
Topic: Daily Life

---

## B. Public Vocabulary

Hiển thị vocabulary công khai lấy từ API hoặc MongoDB.

### Mục đích

* Giúp user học nhanh
* Có sẵn dữ liệu phổ biến
* Không cần tự nhập dữ liệu

### Chức năng

* Xem vocabulary công khai
* Tìm kiếm vocabulary
* Xem theo chủ đề
* Lưu vocabulary về tài khoản cá nhân

---

# 4. Thiết kế Public Vocabulary

# 4.1 Chủ đề phổ biến

Hiển thị theo mức độ phổ biến:

1. TOEIC
2. IELTS
3. Daily English
4. Travel
5. Business
6. Technology
7. Academic
8. JLPT

---

# 4.2 Sắp xếp hiển thị

Ưu tiên hiển thị:

## A. Most Popular

Theo:

* lượt học
* lượt tải
* lượt lưu

---

## B. Theo chủ đề

Ví dụ:

🔥 TOEIC Vocabulary

✈️ Travel Vocabulary

💼 Business English

💻 Technology Terms

---

## C. Theo trình độ

* Beginner
* Intermediate
* Advanced

---

# 5. Chức năng tìm kiếm

# 5.1 Tìm kiếm Personal Vocabulary

Cho phép tìm:

* Word
* Meaning
* Topic

Ví dụ:

* apple
* business
* school

---

# 5.2 Tìm kiếm Public Vocabulary

Cho phép tìm:

* chủ đề
* tên vocabulary
* từ khóa

---

# 6. Chức năng CRUD Vocabulary

# 6.1 Thêm Vocabulary

Người dùng nhập:

* Word
* Meaning
* Pronunciation
* Example
* Topic

Frontend:

* validate dữ liệu
* gọi API backend

Backend:

* lưu MongoDB

---

# 6.2 Sửa Vocabulary

Cho phép cập nhật:

* meaning
* example
* topic

---

# 6.3 Xóa Vocabulary

Người dùng xác nhận xóa.

Backend xóa dữ liệu MongoDB.

---

# 7. Chức năng Import CSV

# 7.1 Mô tả

Cho phép import vocabulary từ file CSV.

---

# 7.2 Định dạng CSV

word,meaning,example,topic

apple,táo,I eat apple,Daily Life

book,sách,This is a book,Education

---

# 7.3 Quy trình hoạt động

Bước 1:
User chọn file CSV.

Bước 2:
Android đọc file CSV.

Bước 3:
Convert thành List Vocabulary.

Bước 4:
Gửi API lên backend.

Bước 5:
Backend lưu MongoDB.

Bước 6:
Trả kết quả thành công.

---

# 8. Chức năng Export CSV

# 8.1 Mô tả

Xuất danh sách vocabulary thành file CSV.

---

# 8.2 Quy trình hoạt động

Bước 1:
Frontend gọi API lấy dữ liệu.

Bước 2:
Convert dữ liệu thành CSV.

Bước 3:
Tạo file CSV trong máy.

Bước 4:
Cho phép user tải xuống.

---

# 9. API công khai sử dụng

# 9.1 Free Dictionary API

Dùng để:

* lấy nghĩa
* phát âm
* ví dụ
* IPA

API:

https://api.dictionaryapi.dev/api/v2/entries/en/{word}

Ví dụ:

https://api.dictionaryapi.dev/api/v2/entries/en/apple

---

# 9.2 Datamuse API

Dùng để:

* gợi ý từ liên quan
* autocomplete
* synonym

API:

https://api.datamuse.com/words?ml=computer

---

# 10. REST API Backend

# 10. REST API Backend

# 10.1 Personal Vocabulary API (backend implemented in ExpressJS)

- GET /api/vocabulary
- POST /api/vocabulary
- PUT /api/vocabulary/:id
- DELETE /api/vocabulary/:id
- POST /api/vocabulary/import  (multipart CSV upload)
- GET  /api/vocabulary/export  (stream CSV download)

---

# 10.2 Public Vocabulary API

- GET /api/public-vocabulary
- GET /api/public-vocabulary/:id
- POST /api/public-vocabulary/:id/save  (save collection to personal vocabulary)

---

# 10.3 Dictionary / Suggest API (proxied external APIs)

- GET /api/dictionary/lookup/:word    -> proxies https://api.dictionaryapi.dev
- GET /api/dictionary/suggest?q=...   -> proxies https://api.datamuse.com

---

# 10.4 Notes on base URL and emulator

- Backend default: `http://localhost:3001/` (server listens on port 3001).
- Android emulator (AVD) should use `http://10.0.2.2:3001/` as `BASE_URL` (see `app/src/main/java/com/example/tegram/di/AppModule.kt`).
- For a physical device, set `BASE_URL` to the machine IP reachable from the device (e.g., `http://192.168.x.y:3001/`).


# 11. Thiết kế MongoDB

# 11.1 Collection users

{
"_id": "ObjectId",
"email": "[user@gmail.com](mailto:user@gmail.com)",
"password": "hashed_password",
"username": "Hao"
}

---

# 11.2 Collection vocabulary

{
"_id": "ObjectId",
"userId": "ObjectId",
"word": "apple",
"meaning": "Táo",
"pronunciation": "/ˈæp.əl/",
"example": "I eat apple",
"topic": "Daily Life",
"isPublic": false,
"createdAt": "2026-05-31"
}

---

# 11.3 Collection public_vocabularies

{
"_id": "ObjectId",
"title": "TOEIC Vocabulary",
"topic": "TOEIC",
"level": "Intermediate",
"totalWords": 600,
"downloads": 15000
}

---

# 12. Luồng hoạt động hệ thống

# 12.1 Khi mở Home

Frontend gọi:

* Personal Vocabulary API
* Public Vocabulary API

Backend:

* query MongoDB
* trả JSON

Frontend:

* hiển thị RecyclerView

---

# 12.2 Khi tìm kiếm

User nhập từ khóa.

Frontend gọi:
GET /api/search/vocabulary?q=apple

Backend:

* query MongoDB
* trả kết quả JSON

Frontend:

* cập nhật realtime RecyclerView

---

# 12.3 Khi thêm vocabulary

Android:

* nhập dữ liệu
* gọi POST API

ExpressJS:

* validate dữ liệu
* lưu MongoDB

MongoDB:

* insert document

---

# 13. Giao diện đề xuất

# Thanh tìm kiếm

[ 🔍 Search vocabulary... ]

---

# Section Home

## 📚 My Vocabulary

Hiển thị vocabulary cá nhân.

---

## 🔥 Popular Vocabulary

Hiển thị:

* TOEIC
* IELTS
* Daily English
* Travel

---

## 🎯 Recommended Topics

Đề xuất chủ đề phổ biến.

---

# 14. Công nghệ sử dụng

# Frontend

* Kotlin
* RecyclerView
* Retrofit
* MVVM
* Material Design

# Backend

* NodeJS
* ExpressJS
* JWT
* Mongoose

# Database

* MongoDB

---

# 15. Kết luận

Module Vocabulary cho phép:

* quản lý vocabulary cá nhân
* quản lý vocabulary công khai
* tìm kiếm nhanh
* import/export CSV
* đồng bộ dữ liệu online

Hệ thống sử dụng:

* Android Studio cho mobile app
* ExpressJS cho backend
* MongoDB cho lưu trữ dữ liệu
* REST API cho giao tiếp hệ thống
