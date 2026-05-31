package com.example.tegram.service.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageHelper @Inject constructor() {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadProfileImage(uid: String, uri: Uri): String {
        return try {
            // Đổi tên file để tránh các ký tự đặc biệt nếu uid là email
            val fileName = uid.replace("@", "_").replace(".", "_")
            val ref = storage.reference.child("profiles/$fileName.jpg")
            
            // Thực hiện upload và đợi kết quả
            val uploadTask = ref.putFile(uri).await()
            
            // Lấy URL sau khi upload thành công
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Lỗi upload ảnh: ${e.message}. Hãy kiểm tra Firebase Storage Rules.")
        }
    }
}
