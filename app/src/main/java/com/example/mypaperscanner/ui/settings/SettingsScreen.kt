package com.example.mypaperscanner.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onShowTutorialClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                "App Info",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            
            ListItem(
                headlineContent = { Text("Show Tutorial") },
                supportingContent = { Text("Walk through the app features again") },
                leadingContent = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                modifier = Modifier.clickable { onShowTutorialClick() }
            )
            
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text("About") },
                supportingContent = { Text("MyPaperScanner v1.0") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
        }
    }
}
