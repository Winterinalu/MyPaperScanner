package com.example.mypaperscanner.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypaperscanner.ui.theme.BrandInk
import com.example.mypaperscanner.ui.theme.BrandInkDark

@Composable
fun NeubrutalistBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowOffset: Dp = 4.dp,
    borderRadius: Dp = 12.dp,
    borderWidth: Dp = 2.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val currentInk = MaterialTheme.colorScheme.onSurface
    
    // Outer padding reserves space for the offset shadow to prevent parent clipping
    Box(
        modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset),
        propagateMinConstraints = true
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape = RoundedCornerShape(borderRadius))
        )
        // Main Box
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(borderRadius))
                .background(backgroundColor, shape = RoundedCornerShape(borderRadius))
                .border(borderWidth, currentInk, shape = RoundedCornerShape(borderRadius)),
            content = content
        )
    }
}

@Composable
fun NeubrutalistButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shadowOffset: Dp = 4.dp,
    borderRadius: Dp = 16.dp,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "ButtonScale")
    val currentInk = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .padding(end = shadowOffset, bottom = shadowOffset)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        propagateMinConstraints = true
    ) {
        // Shadow (disappears when pressed to simulate depth)
        if (!isPressed && enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(currentInk, shape = RoundedCornerShape(borderRadius))
            )
        }

        Surface(
            modifier = Modifier
                .offset(
                    x = if (isPressed) shadowOffset / 2 else 0.dp,
                    y = if (isPressed) shadowOffset / 2 else 0.dp
                )
                .border(2.dp, currentInk, shape = RoundedCornerShape(borderRadius)),
            color = if (enabled) containerColor else Color.Gray,
            contentColor = contentColor,
            shape = RoundedCornerShape(borderRadius)
        ) {
            Row(
                modifier = Modifier
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun StickerBadge(
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    shadowOffset: Dp = 2.dp
) {
    val currentInk = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(currentInk, shape = RoundedCornerShape(6.dp))
        )
        // Badge
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.border(1.5.dp, currentInk, shape = RoundedCornerShape(6.dp))
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = currentInk,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}