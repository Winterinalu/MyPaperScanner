package com.example.mypaperscanner.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mypaperscanner.ui.theme.BrandGreen
import com.example.mypaperscanner.ui.theme.BrandInk
import com.example.mypaperscanner.ui.theme.Error

enum class SnackbarType {
    SUCCESS, INFO, ERROR
}

@Composable
fun BrandSnackbar(
    snackbarData: SnackbarData,
    type: SnackbarType = SnackbarType.INFO
) {
    val accentColor = when (type) {
        SnackbarType.SUCCESS -> BrandGreen
        SnackbarType.ERROR -> Error
        SnackbarType.INFO -> BrandInk
    }

    NeubrutalistBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderRadius = 16.dp,
        shadowOffset = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent indicator
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 24.dp)
                    .background(accentColor, shape = RoundedCornerShape(3.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = snackbarData.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = BrandInk,
                modifier = Modifier.weight(1f)
            )
            
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                TextButton(onClick = { snackbarData.performAction() }) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor
                    )
                }
            }
        }
    }
}
