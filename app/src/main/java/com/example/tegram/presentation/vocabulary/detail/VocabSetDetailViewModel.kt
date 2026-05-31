package com.example.tegram.presentation.vocabulary.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.data.remote.dto.PublicWordDto
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabSetDetailState(
    val isLoading: Boolean = false,
    val words: List<PublicWordDto> = emptyList(),
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class VocabSetDetailViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VocabSetDetailState())
    val state: StateFlow<VocabSetDetailState> = _state.asStateFlow()

    fun loadWords(collectionId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getPublicVocabularyDetails(collectionId, 1, 100)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        words = result.data?.words?.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Failed to load words"
                    )
                }
                else -> { _state.value = _state.value.copy(isLoading = false) }
            }
        }
    }

    fun saveToPersonal(collectionId: String) {
        viewModelScope.launch {
            when (val result = repository.saveToPersonal(collectionId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(saveSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        errorMessage = result.message ?: "Failed to save collection"
                    )
                }
                else -> {}
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, saveSuccess = false)
    }
}
