package com.example.tegram.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditVocabState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val suggestions: List<String> = emptyList()
)

@HiltViewModel
class AddEditVocabViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVocabState())
    val state: StateFlow<AddEditVocabState> = _state.asStateFlow()

    fun saveVocabulary(
        id: String?,
        word: String,
        meaning: String,
        pronunciation: String?,
        example: String?,
        topic: String?,
        isPublic: Boolean
    ) {
        if (word.isBlank() || meaning.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Word and meaning cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val result = if (id == null) {
                repository.createVocabulary(word, meaning, pronunciation, example, topic, isPublic)
            } else {
                repository.updateVocabulary(id, word, meaning, pronunciation, example, topic, isPublic)
            }

            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun lookupWord(word: String, onResult: (String, String) -> Unit) {
        if (word.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.lookupWord(word)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    val data = result.data?.firstOrNull()
                    if (data != null) {
                        val phonetics = data["phonetics"] as? List<Map<String, Any>>
                        val phonetic = phonetics?.firstOrNull { it["text"] != null }?.get("text") as? String ?: ""
                        
                        val meanings = data["meanings"] as? List<Map<String, Any>>
                        val allDefinitions = mutableListOf<String>()
                        meanings?.forEach { meaningDict ->
                            val partOfSpeech = meaningDict["partOfSpeech"] as? String ?: ""
                            val defs = meaningDict["definitions"] as? List<Map<String, Any>>
                            defs?.forEach { def ->
                                val text = def["definition"] as? String
                                if (!text.isNullOrBlank()) {
                                    allDefinitions.add("• [$partOfSpeech] $text")
                                }
                            }
                        }
                        val meaning = allDefinitions.joinToString("\n")
                        
                        onResult(phonetic, meaning)
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Word not found in dictionary")
                }
                else -> { _state.value = _state.value.copy(isLoading = false) }
            }
        }
    }

    fun clearState() {
        _state.value = AddEditVocabState()
    }
}
