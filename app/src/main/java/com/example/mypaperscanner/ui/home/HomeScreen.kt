package com.example.mypaperscanner.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.common.*
import com.example.mypaperscanner.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onScanClick: () -> Unit,
    onImageToPdfClick: () -> Unit,
    onPdfToPictureClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onDocumentClick: (ScannedDocument) -> Unit,
    onSettingsClick: () -> Unit
) {
    val recentDocuments by viewModel.recentDocuments.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(recentDocuments) {
        viewModel.checkAndGenerateThumbnails(context)
    }

    HomeScreenContent(
        recentDocuments = recentDocuments,
        onScanClick = onScanClick,
        onImageToPdfClick = onImageToPdfClick,
        onPdfToPictureClick = onPdfToPictureClick,
        onLibraryClick = onLibraryClick,
        onDocumentClick = onDocumentClick,
        onSettingsClick = onSettingsClick
    )
}

@Composable
fun HomeScreenContent(
    recentDocuments: List<ScannedDocument>,
    onScanClick: () -> Unit,
    onImageToPdfClick: () -> Unit,
    onPdfToPictureClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onDocumentClick: (ScannedDocument) -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Modern Header Section
            HeaderSection(onSettingsClick = onSettingsClick)
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "Good Morning!", 
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Ready to scan some papers?", 
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Hero Section: Scan
                val isDark = MaterialTheme.colorScheme.onBackground == Color.White
                NeubrutalistButton(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    onClick = onScanClick,
                    containerColor = BrandBlue,
                    contentColor = Color.Black,
                    shadowColor = if (isDark) Color.White else Color.Black,
                    borderColor = if (isDark) Color.White else Color.Black,
                    borderRadius = 28.dp,
                    shadowOffset = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "New Scan", 
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Black
                            )
                            Text(
                                "Digitize in seconds", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt, 
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Secondary Actions
                Text(
                    "Quick Tools", 
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ToolCard(
                        title = "Images to PDF",
                        icon = Icons.Default.PictureAsPdf,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onImageToPdfClick,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    ToolCard(
                        title = "PDF to Image",
                        icon = Icons.Default.Image,
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = onPdfToPictureClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Recent Documents
                val brandBlue = if (isDark) BrandBlueDark else BrandBlue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Files", 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onLibraryClick) {
                        Text(
                            "See All", 
                            color = MaterialTheme.colorScheme.primary, 
                            style = MaterialTheme.typography.bodyLarge, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            if (recentDocuments.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 8.dp)
                ) {
                    items(recentDocuments.take(5)) { doc ->
                        RecentStickyCard(
                            document = doc,
                            onClick = { onDocumentClick(doc) },
                            rotation = (doc.id % 6).toFloat() - 3f
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(
                            2.dp, 
                            if (androidx.compose.foundation.isSystemInDarkTheme()) BrandInkDark else BrandInk, 
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No recent scans", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderSection(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "MyPaperScanner",
                style = MaterialTheme.typography.titleLarge.copy(
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
            )
        }
        
        NeubrutalistButton(
            onClick = onSettingsClick,
            containerColor = MaterialTheme.colorScheme.surface,
            borderRadius = 22.dp,
            shadowOffset = 1.5.dp,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                Icons.Default.Settings, 
                contentDescription = "Settings", 
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
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
    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    NeubrutalistButton(
        onClick = onClick,
        containerColor = color,
        contentColor = onColor,
        modifier = modifier.height(110.dp),
        borderRadius = 20.dp,
        shadowOffset = 6.dp,
        shadowColor = if (isDark) Color.White else Color.Black,
        borderColor = if (isDark) Color.White else Color.Black
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(28.dp), 
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                title, 
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
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
    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    
    Box(
        modifier = Modifier
            .rotate(rotation)
            .width(150.dp)
            .padding(vertical = 8.dp)
    ) {
        NeubrutalistBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onClick() },
            backgroundColor = if (isDark) Color.Black else MaterialTheme.colorScheme.surface,
            borderRadius = 8.dp,
            shadowOffset = 6.dp,
            shadowColor = if (isDark) Color.White else Color.Black,
            borderColor = if (isDark) Color.White else Color.Black
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background)
                        .bottomBorder(1.dp, if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
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
                            modifier = Modifier.size(40.dp).align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    
                    // Format Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = if (isPdf) BrandBlue else BrandGreen,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(1.dp, Color.White, shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = if (isPdf) "PDF" else "IMG",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = if (isDark) Color.Black else Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = if (isDark) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${document.pageCount} pages",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = this.drawWithContent {
    drawContent()
    val width = size.width
    val height = size.height
    val strokeWidthPx = strokeWidth.toPx()

    drawLine(
        color = color,
        start = Offset(x = 0f, y = height),
        end = Offset(x = width, y = height),
        strokeWidth = strokeWidthPx
    )
}
