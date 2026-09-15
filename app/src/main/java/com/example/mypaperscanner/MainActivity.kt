package com.example.mypaperscanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypaperscanner.data.AppDatabase
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.DocumentSourceType
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.Screen
import com.example.mypaperscanner.ui.home.HomeScreen
import com.example.mypaperscanner.ui.imagetopdf.ImageToPdfScreen
import com.example.mypaperscanner.ui.library.LibraryScreen
import com.example.mypaperscanner.ui.onboarding.OnboardingScreen
import com.example.mypaperscanner.ui.settings.SettingsScreen
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme
import com.example.mypaperscanner.ui.tutorial.TutorialScreen
import com.example.mypaperscanner.ui.common.BrandDialog
import com.example.mypaperscanner.ui.common.BrandSnackbar
import com.example.mypaperscanner.ui.common.NeubrutalistButton
import com.example.mypaperscanner.ui.common.SuccessCelebration
import com.example.mypaperscanner.util.ImageConverter
import com.example.mypaperscanner.util.PdfConverter
import com.example.mypaperscanner.util.ThumbnailUtils
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var repository: DocumentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        repository = DocumentRepository(database.documentDao())

        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { 
                mutableStateOf(sharedPrefs.getBoolean("is_dark_mode", false)) 
            }
            
            MyPaperScannerTheme(darkTheme = isDarkMode.value) {
                AppNavigation(
                    repository = repository,
                    isDarkMode = isDarkMode.value,
                    onDarkModeChange = { dark ->
                        isDarkMode.value = dark
                        sharedPrefs.edit().putBoolean("is_dark_mode", dark).apply()
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    repository: DocumentRepository,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var hasSeenOnboarding by remember { mutableStateOf(sharedPrefs.getBoolean("has_seen_onboarding", false)) }
    var hasSeenTutorial by remember { mutableStateOf(sharedPrefs.getBoolean("has_seen_tutorial", false)) }
    
    var cameraPermissionGranted by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    var pendingScanResult by remember { mutableStateOf<GmsDocumentScanningResult?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
        if (isGranted) {
            sharedPrefs.edit().putBoolean("has_seen_onboarding", true).apply()
            hasSeenOnboarding = true
            navController.navigate(Screen.Tutorial.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            pendingScanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
        }
    }

    if (pendingScanResult != null) {
        val scanResult = pendingScanResult!!
        BrandDialog(
            onDismissRequest = { pendingScanResult = null },
            title = "Save Scan As",
            text = "Choose the output format for your document. Multi-page scans saved as Images will be split into individual files.",
            confirmButton = {
                NeubrutalistButton(
                    onClick = {
                        val res = pendingScanResult!!
                        pendingScanResult = null
                        scope.launch(Dispatchers.IO) {
                            res.pdf?.let { pdf ->
                                val outputDir = context.getExternalFilesDir(null)
                                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", java.util.Locale.getDefault())
                                val timestamp = sdf.format(java.util.Date())
                                val fileName = "Scan_$timestamp.pdf"
                                val outputFile = File(outputDir, fileName)
                                
                                context.contentResolver.openInputStream(pdf.uri)?.use { input ->
                                    FileOutputStream(outputFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                
                                val doc = ScannedDocument(
                                    title = fileName,
                                    filePath = outputFile.absolutePath,
                                    createdAt = System.currentTimeMillis(),
                                    pageCount = res.pages?.size ?: 0,
                                    sourceType = DocumentSourceType.SCAN,
                                    thumbnailPath = ThumbnailUtils.generateThumbnail(context, outputFile, true)
                                )
                                repository.insertDocument(doc)
                                
                                scope.launch(Dispatchers.Main) {
                                    successMessage = "PDF saved: $fileName"
                                    navController.navigate(Screen.Library.route)
                                }
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text("PDF")
                }
            },
            dismissButton = {
                NeubrutalistButton(
                    onClick = {
                        val res = pendingScanResult!!
                        pendingScanResult = null
                        scope.launch(Dispatchers.IO) {
                            res.pages?.let { pages ->
                                val uris = pages.map { it.imageUri }
                                ImageConverter.saveImagesAsDocuments(
                                    context = context,
                                    imageUris = uris,
                                    repository = repository,
                                    sourceType = DocumentSourceType.SCAN
                                )
                                scope.launch(Dispatchers.Main) {
                                    successMessage = "${pages.size} images saved"
                                    navController.navigate(Screen.Library.route)
                                }
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("Images")
                }
            }
        )
    }

    if (successMessage != null) {
        SuccessCelebration(
            message = successMessage!!,
            onDismiss = { successMessage = null }
        )
    }
    
    val startDestination = if (!hasSeenOnboarding) Screen.Onboarding.route 
                           else if (!hasSeenTutorial) Screen.Tutorial.route
                           else Screen.Home.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }
        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinish = {
                    sharedPrefs.edit().putBoolean("has_seen_tutorial", true).apply()
                    hasSeenTutorial = true
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                onBackClick = { navController.popBackStack() },
                onShowTutorialClick = {
                    navController.navigate(Screen.Tutorial.route)
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.mypaperscanner.ui.home.HomeViewModelFactory(repository)
                ),
                onScanClick = {
                    if (cameraPermissionGranted) {
                        val options = GmsDocumentScannerOptions.Builder()
                            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
                            .setScannerMode(SCANNER_MODE_FULL)
                            .build()
                        val scanner = GmsDocumentScanning.getClient(options)
                        scanner.getStartScanIntent(context as ComponentActivity)
                            .addOnSuccessListener { intentSender ->
                                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                    }
                },
                onImageToPdfClick = { navController.navigate(Screen.ImageToPdf.route) },
                onPdfToPictureClick = { navController.navigate(Screen.PdfToPicture.route) },
                onLibraryClick = { navController.navigate(Screen.Library.route) },
                onDocumentClick = { doc ->
                    navController.navigate(Screen.PdfViewer.createRoute(doc.id))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.mypaperscanner.ui.library.LibraryViewModelFactory(repository)
                ),
                onBackClick = { navController.popBackStack() },
                onDocumentClick = { doc ->
                    navController.navigate(Screen.PdfViewer.createRoute(doc.id))
                },
                onShareClick = { doc ->
                    val file = File(doc.filePath)
                    if (file.exists()) {
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share PDF"))
                    }
                }
            )
        }
        composable(Screen.PdfViewer.route) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId")?.toLongOrNull() ?: 0L
            com.example.mypaperscanner.ui.pdfviewer.PdfViewerScreen(
                docId = docId,
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.mypaperscanner.ui.pdfviewer.PdfViewerViewModelFactory(repository)
                ),
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.PdfToPicture.route) {
            com.example.mypaperscanner.ui.pdftopicture.PdfToPictureScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.mypaperscanner.ui.pdftopicture.PdfToPictureViewModelFactory(repository)
                ),
                onBackClick = { navController.popBackStack() },
                onConversionSuccess = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.ImageToPdf.route) {
            ImageToPdfScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.mypaperscanner.ui.imagetopdf.ImageToPdfViewModelFactory(repository)
                ),
                onBackClick = { navController.popBackStack() },
                onConversionSuccess = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
    }
}
