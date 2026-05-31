package com.example.tegram.presentation.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tegram.data.remote.dto.VocabularyDto

private val TOPICS = listOf("All", "TOEIC", "IELTS", "Daily English", "Travel", "Business", "Technology", "Academic", "JLPT")
private val Primary = Color(0xFF1E88E5)
private val Accent = Color(0xFF2B8CC4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyHomeScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToImportExport: () -> Unit = {},
    viewModel: VocabularyHomeViewModel = hiltViewModel(),
    publicViewModel: PublicVocabularyViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val publicState by publicViewModel.state.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var publicSearchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showAddEdit by remember { mutableStateOf(false) }
    var editingVocabId by remember { mutableStateOf<String?>(null) }
    var deleteConfirmId by remember { mutableStateOf<String?>(null) }
    var selectedTopic by remember { mutableStateOf("All") }

    if (showAddEdit) {
        AddEditVocabularyScreen(
            onNavigateBack = {
                showAddEdit = false
                editingVocabId = null
                viewModel.loadVocabularies()
            },
            viewModel = hiltViewModel(),
            vocabularyId = editingVocabId
        )
        return
    }

    // Delete confirmation dialog
    deleteConfirmId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa từ này không?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteVocabulary(id)
                    deleteConfirmId = null
                }) { Text("Xóa", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📚 Vocabulary",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                ),
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = onNavigateToImportExport) {
                            Icon(Icons.Default.SyncAlt, contentDescription = "Import/Export CSV", tint = Color.White)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = {
                        editingVocabId = null
                        showAddEdit = true
                    },
                    containerColor = Color(0xFF2B8CC4),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Vocabulary")
                }
            }
        },
        containerColor = Color(0xFFF1F5F9)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row (pill style)
            Row(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF0F172A), // Use dark background for visible white tabs
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(modifier = Modifier.padding(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = if (selectedTab == 0) Color(0xFF2B8CC4) else Color.Transparent,
                            modifier = Modifier.clickable { selectedTab = 0 }
                        ) {
                            Text(
                                "My Vocabulary",
                                color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = if (selectedTab == 1) Color(0xFF2B8CC4) else Color.Transparent,
                            modifier = Modifier.clickable { selectedTab = 1 }
                        ) {
                            Text(
                                "Public Library",
                                color = if (selectedTab == 1) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> PersonalVocabularyTab(
                    state = state,
                    searchQuery = searchQuery,
                    onSearchChange = {
                        searchQuery = it
                        viewModel.searchVocabularies(it)
                    },
                    onEdit = { vocab ->
                        editingVocabId = vocab.id
                        showAddEdit = true
                    },
                    onDelete = { deleteConfirmId = it },
                    onLoadNext = { viewModel.loadNextPage() }
                )
                1 -> PublicVocabularyTab(
                    state = publicState,
                    searchQuery = publicSearchQuery,
                    selectedTopic = selectedTopic,
                    onSearchChange = {
                        publicSearchQuery = it
                        publicViewModel.searchCollections(it)
                    },
                    onTopicSelect = { topic ->
                        selectedTopic = topic
                        publicViewModel.filterByTopic(if (topic == "All") null else topic)
                    },
                    onSaveToPersonal = { vocab ->
                        publicViewModel.saveToPersonal(vocab)
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonalVocabularyTab(
    state: VocabularyHomeState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onEdit: (VocabularyDto) -> Unit,
    onDelete: (String) -> Unit,
    onLoadNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search word, meaning, topic...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.isLoading && state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2B8CC4))
                }
            }
            state.errorMessage != null && state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage, color = Color.Red)
                }
            }
            state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No vocabularies yet. Tap + to add one!", color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.vocabularies) { vocab ->
                        PersonalVocabCard(
                            vocabulary = vocab,
                            onEdit = { onEdit(vocab) },
                            onDelete = { onDelete(vocab.id) }
                        )
                    }
                    item {
                        if (state.isLoading) {
                            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        } else if (state.page < state.totalPages) {
                            LaunchedEffect(Unit) { onLoadNext() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalVocabCard(
    vocabulary: VocabularyDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vocabulary.word,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF0F172A)
                    )
                    if (!vocabulary.pronunciation.isNullOrBlank()) {
                        Text(
                            text = "/${vocabulary.pronunciation}/",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2B8CC4))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = vocabulary.meaning,
                color = Color(0xFF334155),
                style = MaterialTheme.typography.bodyLarge
            )

            if (!vocabulary.example.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ex: ${vocabulary.example}",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!vocabulary.topic.isNullOrBlank()) {
                    Surface(
                        color = Primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = vocabulary.topic,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                    }
                }
                if (!vocabulary.createdAt.isNullOrBlank()) {
                    Text(
                        text = vocabulary.createdAt.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun PublicVocabularyTab(
    state: PublicVocabularyState,
    searchQuery: String,
    selectedTopic: String,
    onSearchChange: (String) -> Unit,
    onTopicSelect: (String) -> Unit,
    onSaveToPersonal: (VocabularyDto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search online vocabulary...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Topic filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TOPICS) { topic ->
                FilterChip(
                    selected = selectedTopic == topic,
                    onClick = { onTopicSelect(topic) },
                    label = { Text(topic) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Accent,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Accent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.isLoading && state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF2B8CC4))
                        Spacer(Modifier.height(12.dp))
                        Text("Đang tải từ vựng online...", color = Color(0xFF64748B))
                    }
                }
            }
            state.errorMessage != null && state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage, color = Color.Red)
                }
            }
            state.vocabularies.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Tìm kiếm để khám phá từ vựng online.", color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "🌐 ${state.vocabularies.size} từ vựng từ Internet",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF64748B)
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    items(state.vocabularies) { vocab ->
                        PublicVocabCard(
                            vocabulary = vocab,
                            onSave = { onSaveToPersonal(vocab) }
                        )
                    }
                    if (state.isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Accent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicVocabCard(
    vocabulary: VocabularyDto,
    onSave: () -> Unit
) {
    var saved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vocabulary.word,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF0F172A)
                    )
                    if (!vocabulary.pronunciation.isNullOrBlank()) {
                        Text(
                            text = "/${vocabulary.pronunciation}/",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Button(
                    onClick = {
                        if (!saved) {
                            onSave()
                            saved = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (saved) Color(0xFF22C55E) else Color(0xFF2B8CC4)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (saved) "✓ Saved" else "+ Save",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = vocabulary.meaning,
                color = Color(0xFF334155),
                style = MaterialTheme.typography.bodyLarge
            )

            if (!vocabulary.example.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ex: ${vocabulary.example}",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!vocabulary.topic.isNullOrBlank() && vocabulary.topic != "General") {
                    Surface(
                        color = Primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = vocabulary.topic,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                    }
                }
                if (!vocabulary.createdAt.isNullOrBlank()) {
                    Text(
                        text = vocabulary.createdAt.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
