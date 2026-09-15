package com.example.mypaperscanner.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme

@Preview(showBackground = true)
@Composable
fun BrandDialogThemeTogglePreview() {
    var isDarkTheme by remember { mutableStateOf(false) }
    
    MyPaperScannerTheme(darkTheme = isDarkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Theme Toggle for preview convenience
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isDarkTheme) "Dark Mode" else "Light Mode",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it }
                    )
                }

                NeubrutalistBox(
                    modifier = Modifier.widthIn(max = 320.dp),
                    borderRadius = 24.dp,
                    shadowOffset = 6.dp,
                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Save Scan As",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Choose the output format for your document. Multi-page scans saved as Images will be split into individual files.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NeubrutalistButton(
                                onClick = {},
                                containerColor = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("PDF")
                            }
                            NeubrutalistButton(
                                onClick = {},
                                containerColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Images")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaveScanDialogContentLightPreview() {
    MyPaperScannerTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp).width(320.dp)) {
            NeubrutalistBox(
                borderRadius = 24.dp,
                shadowOffset = 6.dp,
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Save Scan As",
                        style = MaterialTheme.typography.headlineMedium,
                        color = com.example.mypaperscanner.ui.theme.BrandInk
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose the output format for your document. Multi-page scans saved as Images will be split into individual files.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = com.example.mypaperscanner.ui.theme.BrandInk.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NeubrutalistButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PDF")
                        }
                        NeubrutalistButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Images")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaveScanDialogContentDarkPreview() {
    MyPaperScannerTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp).width(320.dp).background(MaterialTheme.colorScheme.background)) {
            NeubrutalistBox(
                borderRadius = 24.dp,
                shadowOffset = 6.dp,
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Save Scan As",
                        style = MaterialTheme.typography.headlineMedium,
                        color = com.example.mypaperscanner.ui.theme.BrandInkDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose the output format for your document. Multi-page scans saved as Images will be split into individual files.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = com.example.mypaperscanner.ui.theme.BrandInkDark.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NeubrutalistButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PDF")
                        }
                        NeubrutalistButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Images")
                        }
                    }
                }
            }
        }
    }
}
