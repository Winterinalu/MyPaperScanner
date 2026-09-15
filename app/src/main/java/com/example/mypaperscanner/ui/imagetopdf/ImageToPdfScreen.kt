package com.example.mypaperscanner.ui.imagetopdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.mypaperscanner.ui.common.PicturePreviewDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageToPdfScreen(
    viewModel: ImageToPdfViewModel = viewModel(factory = ImageToPdfViewModelFactory(
        com.example.mypaperscanner.data.AppDatabase.getDatabase(LocalContext.current).let { 
            com.example.mypaperscanner.data.DocumentRepository(it.documentDao())
        }
    )),
    onBackClick: () -> Unit,
    onConversionSuccess: () -> Unit
) {
    val context = LocalContext.current
    var previewIndex by remember { mutableStateOf<Int?>(null) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        viewModel.onImagesSelected(uris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image to PDF") },
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
                    pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Images")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (viewModel.selectedImages.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(viewModel.selectedImages) { index, uri ->
                        Surface(
                            onClick = { previewIndex = index },
                            shape = MaterialTheme.shapes.small
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                
                previewIndex?.let { index ->
                    PicturePreviewDialog(
                        images = viewModel.selectedImages,
                        initialIndex = index,
                        onDismiss = { previewIndex = null },
                        onRemove = { removeIndex ->
                            val newList = viewModel.selectedImages.toMutableList()
                            newList.removeAt(removeIndex)
                            viewModel.onImagesSelected(newList)
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (viewModel.isConverting) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { viewModel.convert(context, onConversionSuccess) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Convert to PDF")
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No images selected")
                }
            }
        }
    }
}
