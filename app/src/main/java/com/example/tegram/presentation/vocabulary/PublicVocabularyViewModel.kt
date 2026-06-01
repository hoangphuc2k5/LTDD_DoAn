package com.example.tegram.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.data.remote.dto.VocabularyDto
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PublicVocabularyState(
    val isLoading: Boolean = false,
    val vocabularies: List<VocabularyDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class PublicVocabularyViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PublicVocabularyState())
    val state: StateFlow<PublicVocabularyState> = _state.asStateFlow()

    private var currentQuery: String? = null
    private var currentTopic: String? = "popular"

    init {
        loadCollections()
    }

    fun loadCollections(query: String? = null, topic: String? = null) {
        val searchWord = query?.takeIf { it.isNotBlank() } ?: topic?.takeIf { it != "All" } ?: "popular"
        currentQuery = query
        currentTopic = topic
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            when (val suggestResult = repository.suggestWord(searchWord)) {
                is Resource.Success -> {
                    val words = suggestResult.data?.take(15)?.mapNotNull { it["word"] as? String } ?: emptyList()
                    
                    if (words.isEmpty()) {
                        _state.value = _state.value.copy(isLoading = false, vocabularies = emptyList())
                        return@launch
                    }

                    val deferredLookups = words.map { word ->
                        async {
                            val lookupResult = repository.lookupWord(word)
                            if (lookupResult is Resource.Success && !lookupResult.data.isNullOrEmpty()) {
                                val data = lookupResult.data.firstOrNull()
                                val meanings = data?.get("meanings") as? List<Map<String, Any>>
                                val allDefinitions = mutableListOf<String>()
                                meanings?.forEach { meaningDict ->
                                    val partOfSpeech = meaningDict["partOfSpeech"] as? String ?: ""
                                    val defs = meaningDict["definitions"] as? List<Map<String, Any>>
                                    val firstDef = defs?.firstOrNull()?.get("definition") as? String
                                    if (firstDef != null) {
                                        allDefinitions.add("[$partOfSpeech] $firstDef")
                                    }
                                }
                                val finalMeaning = if (allDefinitions.isNotEmpty()) allDefinitions.joinToString("\n") else "No definition available"
                                
                                val phonetics = data?.get("phonetics") as? List<Map<String, Any>>
                                val pronunciation = phonetics?.firstOrNull { (it["text"] as? String)?.isNotBlank() == true }?.get("text") as? String
                                
                                val example = meanings?.firstOrNull()?.let { 
                                    (it["definitions"] as? List<Map<String, Any>>)?.firstOrNull()?.get("example") as? String
                                }

                                VocabularyDto(
                                    id = UUID.randomUUID().toString(),
                                    word = word,
                                    meaning = finalMeaning,
                                    pronunciation = pronunciation,
                                    example = example,
                                    topic = currentTopic ?: "General",
                                    isPublic = true
                                )
                            } else null
                        }
                    }
                    
                    val vocabularies = deferredLookups.awaitAll().filterNotNull()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        vocabularies = vocabularies
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = suggestResult.message ?: "Failed to load public vocabularies"
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun searchCollections(query: String) {
        loadCollections(if (query.isNotBlank()) query else null, currentTopic)
    }

    fun filterByTopic(topic: String?) {
        loadCollections(currentQuery, topic)
    }
    
    fun saveToPersonal(vocabulary: VocabularyDto) {
        viewModelScope.launch {
            repository.createVocabulary(
                word = vocabulary.word,
                meaning = vocabulary.meaning,
                pronunciation = vocabulary.pronunciation,
                example = vocabulary.example,
                topic = vocabulary.topic,
                isPublic = false
            )
        }
    }
}
