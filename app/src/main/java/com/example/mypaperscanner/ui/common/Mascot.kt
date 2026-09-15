package com.example.mypaperscanner.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.mypaperscanner.ui.theme.*

enum class MascotPose {
    WAVING, CAMERA, HAPPY, SEARCHING, UH_OH, CONFUSED
}

@Composable
fun Mascot(
    pose: MascotPose,
    modifier: Modifier = Modifier.size(150.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MascotAnim")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = SineEaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2 + floatAnim

        // Body (Round/Square Bloby shape)
        drawRoundRect(
            color = BrandBlue,
            topLeft = Offset(centerX - w * 0.3f, centerY - h * 0.3f),
            size = Size(w * 0.6f, h * 0.6f),
            cornerRadius = CornerRadius(40f, 40f)
        )
        drawRoundRect(
            color = BrandInk,
            topLeft = Offset(centerX - w * 0.3f, centerY - h * 0.3f),
            size = Size(w * 0.6f, h * 0.6f),
            cornerRadius = CornerRadius(40f, 40f),
            style = Stroke(width = 6f)
        )

        // Face - Eyes
        drawCircle(
            color = BrandInk,
            radius = 8f,
            center = Offset(centerX - w * 0.1f, centerY - h * 0.05f)
        )
        drawCircle(
            color = BrandInk,
            radius = 8f,
            center = Offset(centerX + w * 0.1f, centerY - h * 0.05f)
        )

        // Pose Specifics
        when (pose) {
            MascotPose.WAVING -> {
                // Arm waving
                drawRoundRect(
                    color = BrandGreen,
                    topLeft = Offset(centerX + w * 0.25f, centerY - h * 0.1f),
                    size = Size(w * 0.2f, h * 0.1f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = BrandInk,
                    topLeft = Offset(centerX + w * 0.25f, centerY - h * 0.1f),
                    size = Size(w * 0.2f, h * 0.1f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 4f)
                )
                // Smile
                drawArc(
                    color = BrandInk,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 20f, centerY + 10f),
                    size = Size(40f, 20f),
                    style = Stroke(width = 4f)
                )
            }
            MascotPose.CAMERA -> {
                // Holding a small camera
                drawRoundRect(
                    color = BrandYellow,
                    topLeft = Offset(centerX - w * 0.15f, centerY + h * 0.05f),
                    size = Size(w * 0.3f, h * 0.2f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = BrandInk,
                    topLeft = Offset(centerX - w * 0.15f, centerY + h * 0.05f),
                    size = Size(w * 0.3f, h * 0.2f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 4f)
                )
                drawCircle(color = BrandInk, radius = 15f, center = Offset(centerX, centerY + h * 0.15f))
            }
            MascotPose.HAPPY -> {
                // Wide Smile
                drawArc(
                    color = BrandYellow,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(centerX - 30f, centerY + 10f),
                    size = Size(60f, 40f)
                )
                drawArc(
                    color = BrandInk,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 30f, centerY + 10f),
                    size = Size(60f, 40f),
                    style = Stroke(width = 4f)
                )
            }
            MascotPose.SEARCHING -> {
                // One eye bigger
                drawCircle(
                    color = BrandInk,
                    radius = 12f,
                    center = Offset(centerX - w * 0.1f, centerY - h * 0.05f)
                )
                // Straight mouth
                drawLine(
                    color = BrandInk,
                    start = Offset(centerX - 20f, centerY + 30f),
                    end = Offset(centerX + 20f, centerY + 30f),
                    strokeWidth = 4f
                )
            }
            MascotPose.UH_OH -> {
                // Wide eyes
                drawCircle(color = BrandInk, radius = 10f, center = Offset(centerX - w * 0.12f, centerY - h * 0.05f))
                drawCircle(color = BrandInk, radius = 10f, center = Offset(centerX + w * 0.12f, centerY - h * 0.05f))
                // O-shaped mouth
                drawCircle(
                    color = BrandInk,
                    radius = 15f,
                    center = Offset(centerX, centerY + 25f),
                    style = Stroke(width = 4f)
                )
            }
            MascotPose.CONFUSED -> {
                // One eyebrow up
                drawLine(
                    color = BrandInk,
                    start = Offset(centerX - w * 0.15f, centerY - h * 0.15f),
                    end = Offset(centerX - w * 0.05f, centerY - h * 0.12f),
                    strokeWidth = 4f
                )
                // Wavy mouth
                drawArc(
                    color = BrandInk,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 20f, centerY + 20f),
                    size = Size(20f, 10f),
                    style = Stroke(width = 4f)
                )
                drawArc(
                    color = BrandInk,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX, centerY + 20f),
                    size = Size(20f, 10f),
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}

val SineEaseInOut = Easing { fraction ->
    ((1 - Math.cos(fraction * Math.PI)) / 2).toFloat()
}
