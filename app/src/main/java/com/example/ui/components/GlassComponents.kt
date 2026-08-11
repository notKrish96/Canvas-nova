package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.WallpaperEntity
import com.example.ui.theme.*
import com.example.utils.WallpaperHelper

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = GlassCardBg,
    borderColor: Color = GlassCardBorder,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1AFFFFFF),
                        Color(0x05FFFFFF)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.35f),
                            borderColor.copy(alpha = 0.08f)
                        )
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(18.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlassChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) ElectricCyan.copy(alpha = 0.22f) else Color(0x0DFFFFFF),
        label = "chipBg"
    )
    val animatedBorder by animateColorAsState(
        targetValue = if (isSelected) ElectricCyan else Color(0x1AFFFFFF),
        label = "chipBorder"
    )
    val animatedText by animateColorAsState(
        targetValue = if (isSelected) AccentBlue else TextSecondary,
        label = "chipText"
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(animatedBg)
            .border(1.dp, animatedBorder, CircleShape)
            .clickable {
                WallpaperHelper.triggerHaptic(haptic)
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = animatedText,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun WallpaperItemCard(
    wallpaper: WallpaperEntity,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onApplyQuick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0x33FFFFFF),
                        Color(0x0AFFFFFF)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .clickable {
                WallpaperHelper.triggerHaptic(haptic)
                onClick()
            }
            .testTag("wallpaper_item_${wallpaper.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0x0DFFFFFF))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Wallpaper Image
            AsyncImage(
                model = wallpaper.imageUrl,
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Glass Overlay Gradient at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Badges (Resolution & Favorite)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Resolution Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, Color(0x33FFFFFF))
                ) {
                    Text(
                        text = wallpaper.resolution,
                        color = AccentBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite Heart Button
                IconButton(
                    onClick = { onFavoriteToggle() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (wallpaper.isFavorite) NeonMagenta else Color.White
                    )
                }
            }

            // Bottom Title & Action Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = wallpaper.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#" + wallpaper.category,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )

                    // Quick Apply Icon
                    IconButton(
                        onClick = { onApplyQuick() },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.25f))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = "Apply Wallpaper",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
