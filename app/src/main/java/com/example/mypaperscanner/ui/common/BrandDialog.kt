package com.example.mypaperscanner.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mypaperscanner.ui.theme.BrandInk

@Composable
fun BrandDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    mascotPose: MascotPose? = null,
    properties: DialogProperties = DialogProperties()
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "DialogFloat")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = com.example.mypaperscanner.ui.common.SineEaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialScale = 0.8f
            ),
            exit = scaleOut(targetScale = 0.8f)
        ) {
            NeubrutalistBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = floatAnim.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                borderRadius = 24.dp,
                shadowOffset = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (mascotPose != null) {
                        Mascot(
                            pose = mascotPose,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BrandInk,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrandInk.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        confirmButton()
                        dismissButton?.invoke()
                    }
                }
            }
        }
    }
}
