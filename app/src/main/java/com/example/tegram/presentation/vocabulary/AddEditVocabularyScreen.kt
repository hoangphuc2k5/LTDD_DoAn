package com.example.tegram.presentation.vocabulary

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tegram.presentation.vocabulary.addword.AddWordScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVocabularyScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVocabViewModel = hiltViewModel(),
    vocabularyId: String? = null
) {
    AddWordScreen(
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        vocabularyId = vocabularyId
    )
}
