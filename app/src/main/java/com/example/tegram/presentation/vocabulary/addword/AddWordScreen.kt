package com.example.tegram.presentation.vocabulary.addword

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tegram.presentation.vocabulary.AddEditVocabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVocabViewModel = hiltViewModel(),
    vocabularyId: String? = null
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
            viewModel.clearState()
            onNavigateBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vocabularyId == null) "Add Vocabulary" else "Edit Vocabulary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F5F9))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Auto-Lookup Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Word Auto-Lookup",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = word,
                            onValueChange = { word = it },
                            label = { Text("Word *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledIconButton(
                            onClick = {
                                viewModel.lookupWord(word) { p, m ->
                                    pronunciation = p
                                    meaning = m
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFF2B8CC4)
                            )
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Lookup", tint = Color.White)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("Meaning *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pronunciation,
                onValueChange = { pronunciation = it },
                label = { Text("Pronunciation (e.g. /ˈæp.əl/)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = example,
                onValueChange = { example = it },
                label = { Text("Example Sentence") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topic (e.g. Daily, TOEIC, IELTS)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
                Spacer(modifier = Modifier.width(4.dp))
                Text("Make this word public")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.saveVocabulary(
                        id = vocabularyId,
                        word = word.trim(),
                        meaning = meaning.trim(),
                        pronunciation = pronunciation.trim().ifBlank { null },
                        example = example.trim().ifBlank { null },
                        topic = topic.trim().ifBlank { null },
                        isPublic = isPublic
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B8CC4)),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Vocabulary", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
