package com.example.mypaperscanner.ui.scan

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypaperscanner.data.AppDatabase
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.ui.common.PicturePreviewDialog
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(factory = ScanViewModelFactory(
        AppDatabase.getDatabase(LocalContext.current).let { 
            DocumentRepository(it.documentDao())
        }
    )),
    onBackClick: () -> Unit,
    onScanFinished: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var showPreview by remember { mutableStateOf<Int?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also {
                    previewView = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(previewView) {
            val view = previewView ?: return@LaunchedEffect
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
            
            imageCapture = ImageCapture.Builder().build()
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // UI Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
            }
            
            Button(
                onClick = {
                    val capture = imageCapture ?: return@Button
                    val photoFile = File(
                        context.getExternalFilesDir(null),
                        "Scan_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    
                    capture.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                viewModel.addImage(savedUri)
                            }
                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture", tint = Color.Black, modifier = Modifier.size(40.dp))
            }
            
            if (viewModel.capturedImages.isNotEmpty()) {
                BadgedBox(
                    badge = { Badge { Text(viewModel.capturedImages.size.toString()) } }
                ) {
                    IconButton(onClick = { showPreview = 0 }) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        
        if (viewModel.isConverting) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        
        showPreview?.let { index ->
            PicturePreviewDialog(
                images = viewModel.capturedImages,
                initialIndex = index,
                onDismiss = { showPreview = null },
                onRemove = { removeIndex ->
                    viewModel.removeImage(removeIndex)
                }
            )
            
            // Add a "Finish" button in the dialog or here
            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                Button(onClick = { 
                    showPreview = null
                    viewModel.finishScan(context, onScanFinished)
                }) {
                    Text("Finish (${viewModel.capturedImages.size})")
                }
            }
        }
    }
}
