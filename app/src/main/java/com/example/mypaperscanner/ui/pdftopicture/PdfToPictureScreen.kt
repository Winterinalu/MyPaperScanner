package com.example.mypaperscanner.ui.pdftopicture

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypaperscanner.data.AppDatabase
import com.example.mypaperscanner.data.DocumentRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToPictureScreen(
    viewModel: PdfToPictureViewModel = viewModel(factory = PdfToPictureViewModelFactory(
        AppDatabase.getDatabase(LocalContext.current).let { 
            DocumentRepository(it.documentDao())
        }
    )),
    onBackClick: () -> Unit,
    onConversionSuccess: () -> Unit
) {
    val context = LocalContext.current
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        viewModel.onPdfSelected(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF to Picture") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    pickerLauncher.launch(arrayOf("application/pdf"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (viewModel.selectedPdf == null) "Select PDF" else "Change PDF")
            }
            
            viewModel.selectedPdf?.let { uri ->
                Text(
                    text = "Selected: ${uri.path?.split("/")?.lastOrNull() ?: "Document"}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Output Format", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = viewModel.outputFormat == Bitmap.CompressFormat.JPEG,
                    onClick = { viewModel.outputFormat = Bitmap.CompressFormat.JPEG }
                )
                Text("JPG")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = viewModel.outputFormat == Bitmap.CompressFormat.PNG,
                    onClick = { viewModel.outputFormat = Bitmap.CompressFormat.PNG }
                )
                Text("PNG")
            }
            
            if (viewModel.outputFormat == Bitmap.CompressFormat.JPEG) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Quality: ${viewModel.quality}%")
                Slider(
                    value = viewModel.quality.toFloat(),
                    onValueChange = { viewModel.quality = it.toInt() },
                    valueRange = 0f..100f
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (viewModel.isConverting) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.convert(context, onConversionSuccess) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.selectedPdf != null
                ) {
                    Text("Convert to Images")
                }
            }
        }
    }
}
