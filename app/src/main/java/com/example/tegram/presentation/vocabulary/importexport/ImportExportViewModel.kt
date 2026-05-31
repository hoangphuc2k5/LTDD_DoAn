package com.example.tegram.presentation.vocabulary.importexport

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class ImportExportState(
    val isLoading: Boolean = false,
    val isImporting: Boolean = false,
    val importSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ImportExportViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ImportExportState())
    val state: StateFlow<ImportExportState> = _state.asStateFlow()

    fun importCSV(file: MultipartBody.Part) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isImporting = true, errorMessage = null)
            when (val result = repository.importCSV(file)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isImporting = false,
                        importSuccess = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isImporting = false,
                        errorMessage = result.message ?: "Import failed"
                    )
                }
                else -> { _state.value = _state.value.copy(isLoading = false, isImporting = false) }
            }
        }
    }

    fun exportCSV(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isImporting = false, errorMessage = null)
            when (val result = repository.exportCSV()) {
                is Resource.Success -> {
                    val body = result.data
                    if (body != null) {
                        try {
                            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                            val file = File(downloadsDir, "vocabulary_export.csv")
                            FileOutputStream(file).use { fos ->
                                fos.write(body.bytes())
                            }
                            // Share file
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export Vocabulary CSV"))
                            _state.value = _state.value.copy(isLoading = false)
                        } catch (e: Exception) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = "Failed to save file: ${e.message}"
                            )
                        }
                    } else {
                        _state.value = _state.value.copy(isLoading = false, errorMessage = "Empty response")
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Export failed"
                    )
                }
                else -> { _state.value = _state.value.copy(isLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, importSuccess = false)
    }
}
