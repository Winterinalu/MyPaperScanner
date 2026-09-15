package com.example.mypaperscanner.ui.pdfviewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage
import com.example.mypaperscanner.data.AppDatabase
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.ui.common.BrandSnackbar
import com.example.mypaperscanner.ui.common.SnackbarType
import com.example.mypaperscanner.util.StorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    docId: Long,
    viewModel: PdfViewerViewModel = viewModel(factory = PdfViewerViewModelFactory(
        AppDatabase.getDatabase(LocalContext.current).let { 
            DocumentRepository(it.documentDao())
        }
    )),
    onBackClick: () -> Unit
) {
    LaunchedEffect(docId) {
        viewModel.loadDocument(docId)
    }

    val doc = viewModel.document
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let {
            doc?.let { d ->
                val success = StorageUtils.saveFileToUri(context, d.filePath, it)
                if (success) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Saved successfully")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to save")
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                BrandSnackbar(snackbarData = data, type = SnackbarType.SUCCESS)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(doc?.title ?: "Document Viewer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (doc != null) {
                        val isPdf = doc.filePath.endsWith(".pdf", ignoreCase = true)
                        
                        IconButton(onClick = {
                            if (isPdf) {
                                viewModel.convertToImage(context) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Converted to images")
                                    }
                                }
                            } else {
                                viewModel.convertToPdf(context) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Converted to PDF")
                                    }
                                }
                            }
                        }) {
                            Icon(
                                if (isPdf) Icons.Default.Image else Icons.Default.PictureAsPdf,
                                contentDescription = "Convert",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = {
                            saveLauncher.launch(doc.title)
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading || viewModel.isConverting) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (doc != null) {
            val file = File(doc.filePath)
            if (file.exists()) {
                if (doc.title.endsWith(".jpg", ignoreCase = true) || doc.title.endsWith(".png", ignoreCase = true)) {
                    Box(modifier = Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = file,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    PdfContent(file, paddingValues)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("File not found")
                }
            }
        }
    }
}

@Composable
fun PdfContent(file: File, paddingValues: PaddingValues) {
    val pfd = remember(file) { ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) }
    val renderer = remember(pfd) { PdfRenderer(pfd) }
    val pageCount = renderer.pageCount
    val pagerState = rememberPagerState(pageCount = { pageCount })

    DisposableEffect(renderer) {
        onDispose {
            renderer.close()
            pfd.close()
        }
    }

    Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            PdfPage(renderer, pageIndex)
        }
        
        Text(
            text = "Page ${pagerState.currentPage + 1} of $pageCount",
            modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PdfPage(renderer: PdfRenderer, pageIndex: Int) {
    val bitmapState = produceState<Bitmap?>(initialValue = null, renderer, pageIndex) {
        value = withContext(Dispatchers.IO) {
            renderer.openPage(pageIndex).use { page ->
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmap
            }
        }
    }

    bitmapState.value?.let { bitmap ->
        ZoomableImage(bitmap)
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ZoomableImage(bitmap: Bitmap) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = state)
            .graphicsLayer(
                scaleX = scale.coerceIn(1f, 5f),
                scaleY = scale.coerceIn(1f, 5f),
                translationX = offset.x,
                translationY = offset.y
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
