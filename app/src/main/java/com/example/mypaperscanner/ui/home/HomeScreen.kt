package com.example.mypaperscanner.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.common.*
import com.example.mypaperscanner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onScanClick: () -> Unit,
    onImageToPdfClick: () -> Unit,
    onPdfToPictureClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onDocumentClick: (ScannedDocument) -> Unit,
    cameraPermissionGranted: Boolean,
    onSettingsClick: () -> Unit
) {
    val recentDocuments by viewModel.recentDocuments.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(recentDocuments) {
        viewModel.checkAndGenerateThumbnails(context)
    }

    Scaffold(
        containerColor = BrandBackground,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BrandBackground),
                title = { 
                    Text("MyPaperScanner", style = MaterialTheme.typography.titleLarge, color = BrandInk) 
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = BrandInk)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Good Morning!", 
                style = MaterialTheme.typography.displayLarge,
                color = BrandInk
            )
            Text(
                "Ready to scan some papers?", 
                style = MaterialTheme.typography.bodyLarge,
                color = BrandInk.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Hero Section: Scan
            NeubrutalistButton(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                onClick = onScanClick,
                containerColor = BrandBlue,
                borderRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            "New Scan", 
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            "Digitize in seconds", 
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, 
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Secondary Actions
            Text(
                "Quick Tools", 
                style = MaterialTheme.typography.titleLarge,
                color = BrandInk
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ToolCard(
                    title = "Images to PDF",
                    icon = Icons.Default.PictureAsPdf,
                    color = BrandGreen,
                    onClick = onImageToPdfClick,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                ToolCard(
                    title = "PDF to Image",
                    icon = Icons.Default.Image,
                    color = BrandYellow,
                    onClick = onPdfToPictureClick,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Recent Documents
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Files", 
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandInk
                )
                TextButton(onClick = onLibraryClick) {
                    Text("See All", color = BrandBlue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
            
            if (recentDocuments.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(recentDocuments.take(5)) { doc ->
                        RecentStickyCard(
                            document = doc,
                            onClick = { onDocumentClick(doc) },
                            rotation = (doc.id % 6 - 3).toFloat() // Randomish tilt
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .border(2.dp, BrandInk, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No recent scans", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandInk.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeubrutalistButton(
        onClick = onClick,
        containerColor = color,
        modifier = modifier.height(100.dp),
        borderRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = BrandInk)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title, 
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = BrandInk,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RecentStickyCard(
    document: ScannedDocument,
    onClick: () -> Unit,
    rotation: Float
) {
    val isPdf = document.filePath.endsWith(".pdf", ignoreCase = true)
    
    Box(
        modifier = Modifier
            .rotate(rotation)
            .width(140.dp)
    ) {
        NeubrutalistBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onClick() },
            backgroundColor = Color.White,
            borderRadius = 4.dp // Sticky note style
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(BrandBackground)
                ) {
                    if (document.thumbnailPath != null) {
                        AsyncImage(
                            model = document.thumbnailPath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).align(Alignment.Center),
                            tint = BrandInk.copy(alpha = 0.3f)
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${document.pageCount} pg",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandInk.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
