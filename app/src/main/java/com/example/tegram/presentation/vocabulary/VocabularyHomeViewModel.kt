package com.example.tegram.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.data.remote.dto.VocabularyDto
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyHomeState(
    val isLoading: Boolean = false,
    val vocabularies: List<VocabularyDto> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val page: Int = 1,
    val totalPages: Int = 1
)

@HiltViewModel
class VocabularyHomeViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VocabularyHomeState())
    val state: StateFlow<VocabularyHomeState> = _state.asStateFlow()

    private var currentQuery: String? = null

    init {
        loadVocabularies()
    }

    fun loadVocabularies(query: String? = null, page: Int = 1) {
        currentQuery = query
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            when (val result = repository.getVocabularies(page, 20, query)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        vocabularies = if (page == 1) result.data?.data ?: emptyList() else _state.value.vocabularies + (result.data?.data ?: emptyList()),
                        page = result.data?.page ?: 1,
                        totalPages = result.data?.pages ?: 1
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Failed to load vocabularies"
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun loadNextPage() {
        if (_state.value.page < _state.value.totalPages && !_state.value.isLoading) {
            loadVocabularies(currentQuery, _state.value.page + 1)
        }
    }

    fun searchVocabularies(query: String) {
        loadVocabularies(if (query.isNotBlank()) query else null, 1)
    }

    fun deleteVocabulary(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.deleteVocabulary(id)) {
                is Resource.Success -> {
                    // Reload first page after delete
                    loadVocabularies(currentQuery, 1)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Failed to delete vocabulary"
                    )
                }
                else -> { _state.value = _state.value.copy(isLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}
