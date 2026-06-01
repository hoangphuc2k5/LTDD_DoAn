package com.example.tegram.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.repository.UserRepository
import com.example.tegram.service.storage.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val storageHelper: StorageHelper
) : ViewModel() {

    val user: StateFlow<UserProfile?> = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val reminderTime: StateFlow<String?> = userPreferencesDataStore.reminderTimeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun updateProfile(fullName: String, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                var photoUrl: String? = null
                if (imageUri != null) {
                    val uid = user.value?.uid ?: return@launch
                    photoUrl = storageHelper.uploadProfileImage(uid, imageUri)
                }
                
                userRepository.updateProfile(
                    fullName = fullName.takeIf { it.isNotBlank() },
                    photoUrl = photoUrl
                )
                _uiState.value = ProfileUiState.Success("Cập nhật thành công")
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Có lỗi xảy ra")
            }
        }
    }

    fun saveReminderTime(time: String) {
        viewModelScope.launch {
            userPreferencesDataStore.saveReminderTime(time)
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val message: String) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
