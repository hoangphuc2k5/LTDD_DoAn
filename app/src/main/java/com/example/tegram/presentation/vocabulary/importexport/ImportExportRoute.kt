package com.example.tegram.presentation.vocabulary.importexport

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ImportExportRoute(
    onNavigateBack: () -> Unit,
    viewModel: ImportExportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.importSuccess) {
        if (state.importSuccess) {
            Toast.makeText(context, "Import successful!", Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it) ?: return@let
            val tempFile = File(context.cacheDir, "import_vocab.csv")
            tempFile.outputStream().use { out -> inputStream.copyTo(out) }

            val requestBody = tempFile.asRequestBody("text/csv".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
            viewModel.importCSV(part)
        }
    }

    ImportExportScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onPickFile = { pickFileLauncher.launch("text/*") },
        onExport = { viewModel.exportCSV(context) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    state: ImportExportState,
    onNavigateBack: () -> Unit,
    onPickFile: () -> Unit,
    onExport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import / Export CSV", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFFF1F5F9)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Import Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = null,
                            tint = Color(0xFF2B8CC4),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Import CSV",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Upload a CSV file to add vocabulary in bulk.\n\nFormat: word, meaning, example, topic",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onPickFile,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B8CC4)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading && state.isImporting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose CSV File")
                        }
                    }
                }
            }

            // Export Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Export CSV",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Export all your vocabulary to a CSV file and save it to your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onExport,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading && !state.isImporting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Export to CSV")
                        }
                    }
                }
            }

            // CSV Format guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📄 CSV Format Guide", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "word,meaning,example,topic\napple,táo,I eat apple,Daily Life\nbook,sách,This is a book,Education",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }
    }
}
