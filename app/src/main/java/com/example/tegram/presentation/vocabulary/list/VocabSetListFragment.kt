package com.example.tegram.presentation.vocabulary.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tegram.presentation.vocabulary.VocabularyHomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VocabSetListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                VocabularyHomeScreen(
                    onNavigateToAdd = {},
                    onNavigateToDetail = { collectionId ->
                        // Navigate to detail if nav graph is configured
                    }
                )
            }
        }
    }
}
