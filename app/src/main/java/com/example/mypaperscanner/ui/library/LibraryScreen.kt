package com.example.mypaperscanner.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.common.*
import com.example.mypaperscanner.ui.theme.*
import com.example.mypaperscanner.util.StorageUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(factory = LibraryViewModelFactory(
        com.example.mypaperscanner.data.AppDatabase.getDatabase(LocalContext.current).let { 
            com.example.mypaperscanner.data.DocumentRepository(it.documentDao())
        }
    )),
    onBackClick: () -> Unit,
    onDocumentClick: (ScannedDocument) -> Unit,
    onShareClick: (ScannedDocument) -> Unit
) {
    val documents by viewModel.documents.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(documents) {
        viewModel.checkAndGenerateThumbnails(context)
    }
    
    var documentToSave by remember { mutableStateOf<ScannedDocument?>(null) }
    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let {
            documentToSave?.let { doc ->
                val success = StorageUtils.saveFileToUri(context, doc.filePath, it)
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
        documentToSave = null
    }

    var documentToDelete by remember { mutableStateOf<ScannedDocument?>(null) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    if (documentToDelete != null) {
        BrandDialog(
            onDismissRequest = { documentToDelete = null },
            title = "Delete Document?",
            text = "This will permanently remove '${documentToDelete?.title}'. This action cannot be undone.",
            mascotPose = MascotPose.UH_OH,
            confirmButton = {
                NeubrutalistButton(
                    onClick = {
                        documentToDelete?.let { viewModel.deleteDocument(it) }
                        documentToDelete = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Document deleted")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text("Delete Anyway", color = Color.White)
                }
            },
            dismissButton = {
                NeubrutalistButton(
                    onClick = { documentToDelete = null },
                    containerColor = Color.White
                ) {
                    Text("Keep It", color = BrandInk)
                }
            }
        )
    }

    if (showBulkDeleteDialog) {
        BrandDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = "Delete Selected?",
            text = "Are you sure you want to delete ${viewModel.selectedIds.size} documents?",
            mascotPose = MascotPose.UH_OH,
            confirmButton = {
                NeubrutalistButton(
                    onClick = {
                        viewModel.deleteSelected()
                        showBulkDeleteDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Documents deleted")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text("Delete All", color = Color.White)
                }
            },
            dismissButton = {
                NeubrutalistButton(
                    onClick = { showBulkDeleteDialog = false },
                    containerColor = Color.White
                ) {
                    Text("Cancel", color = BrandInk)
                }
            }
        )
    }

    Scaffold(
        containerColor = BrandBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                BrandSnackbar(snackbarData = data, type = SnackbarType.SUCCESS)
            }
        },
        topBar = {
            if (viewModel.isSelectionMode) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBackground),
                    title = { Text("${viewModel.selectedIds.size} selected", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = BrandInk)
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            val selectedDocs = documents.filter { it.id in viewModel.selectedIds }
                            if (selectedDocs.isNotEmpty()) {
                                val uris = selectedDocs.map { doc ->
                                    androidx.core.content.FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        java.io.File(doc.filePath)
                                    )
                                }
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND_MULTIPLE).apply {
                                    type = "application/pdf"
                                    putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, ArrayList(uris))
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share Documents"))
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = BrandInk)
                        }
                        IconButton(onClick = { showBulkDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandInk)
                        }
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BrandBackground),
                    title = { Text("My Library", style = MaterialTheme.typography.headlineMedium, color = BrandInk) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandInk)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Mascot(pose = MascotPose.SEARCHING)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Nothing here yet...",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BrandInk
                    )
                    Text(
                        "Let's fix that! Start scanning.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrandInk.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(documents) { doc ->
                    DocumentGridItem(
                        document = doc,
                        selected = viewModel.selectedIds.contains(doc.id),
                        onClick = { 
                            if (viewModel.isSelectionMode) {
                                viewModel.toggleSelection(doc.id)
                            } else {
                                onDocumentClick(doc)
                            }
                        },
                        onLongClick = { viewModel.toggleSelection(doc.id) },
                        onDelete = { documentToDelete = doc },
                        onShare = { onShareClick(doc) },
                        onExportAsImage = {
                            viewModel.exportToImage(context, doc) {}
                        },
                        onConvertToPdf = {
                            viewModel.convertToPdf(context, doc) {}
                        },
                        onSaveToDownloads = {
                            documentToSave = doc
                            saveLauncher.launch(doc.title)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DocumentGridItem(
    document: ScannedDocument,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onExportAsImage: () -> Unit,
    onConvertToPdf: () -> Unit,
    onSaveToDownloads: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val isPdf = document.filePath.endsWith(".pdf", ignoreCase = true)
    
    NeubrutalistBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.White,
        borderRadius = 12.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BrandBackground)
            ) {
                if (document.thumbnailPath != null) {
                    AsyncImage(
                        model = document.thumbnailPath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).align(Alignment.Center),
                        tint = BrandInk.copy(alpha = 0.2f)
                    )
                }
                
                // Sticker badge
                StickerBadge(
                    text = if (isPdf) "PDF" else "IMG",
                    containerColor = if (isPdf) BrandGreen else BrandYellow,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                )
                
                if (selected) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(BrandBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = document.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandInk,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(16.dp), tint = BrandInk)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${dateFormat.format(Date(document.createdAt))} • ${document.pageCount} pg",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandInk.copy(alpha = 0.6f)
                )
            }
        }
        
        Box {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(8.dp)
            ) {
                NeubrutalistBox(
                    modifier = Modifier.width(200.dp),
                    backgroundColor = Color.White,
                    borderRadius = 16.dp,
                    shadowOffset = 4.dp
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        DropdownMenuItem(
                            text = { Text("Share", style = MaterialTheme.typography.bodyMedium) },
                            onClick = { showMenu = false; onShare() },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        if (isPdf) {
                            DropdownMenuItem(
                                text = { Text("Export as Image", style = MaterialTheme.typography.bodyMedium) },
                                onClick = { showMenu = false; onExportAsImage() },
                                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Convert to PDF", style = MaterialTheme.typography.bodyMedium) },
                                onClick = { showMenu = false; onConvertToPdf() },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Save to Downloads", style = MaterialTheme.typography.bodyMedium) },
                            onClick = { showMenu = false; onSaveToDownloads() },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DropdownMenuItem(
                            text = { Text("Delete", style = MaterialTheme.typography.bodyMedium) },
                            onClick = { showMenu = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error,
                                leadingIconColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
}
