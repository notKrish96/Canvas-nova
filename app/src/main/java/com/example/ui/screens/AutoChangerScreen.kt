package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutoChangerSettingsEntity
import com.example.data.CategoryEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.theme.*
import com.example.utils.WallpaperHelper

@Composable
fun AutoChangerScreen(
    settings: AutoChangerSettingsEntity?,
    categories: List<CategoryEntity>,
    nextChangeSecondsLeft: Int,
    onIntervalChange: (Int) -> Unit,
    onCategoryChange: (String) -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onTriggerNow: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val currentSettings = settings ?: AutoChangerSettingsEntity()

    val intervals = listOf(
        Pair(5, "5 Min (Default)"),
        Pair(15, "15 Min"),
        Pair(30, "30 Min"),
        Pair(60, "1 Hour"),
        Pair(360, "6 Hours"),
        Pair(1440, "24 Hours")
    )

    val tagFilterOptions = listOf("All", "dark", "oled", "4k", "glass", "neon")
    var selectedTagFilter by remember { mutableStateOf("All") }

    val resolutionOptions = listOf("All", "4K (3840x2160)", "1440p", "1080p")
    var selectedResolutionFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AUTO WALLPAPER CHANGER",
                    color = ElectricCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Automatically cycle wallpapers on interval",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Master Switch Card with Timer Status
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (currentSettings.isEnabled) ElectricCyan else Color(0x33FFFFFF)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentSettings.isEnabled) "Auto-Changer Active" else "Auto-Changer Paused",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Interval: ${currentSettings.intervalMinutes} Minutes",
                        color = ElectricCyan,
                        fontSize = 13.sp
                    )
                }

                Switch(
                    checked = currentSettings.isEnabled,
                    onCheckedChange = { isChecked ->
                        WallpaperHelper.triggerHaptic(haptic)
                        onToggleEnabled(isChecked)
                    },
                    modifier = Modifier.testTag("auto_changer_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = ElectricCyan
                    )
                )
            }

            if (currentSettings.isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))

                val minutes = nextChangeSecondsLeft / 60
                val seconds = nextChangeSecondsLeft % 60
                val timerDisplay = String.format("%02d:%02d", minutes, seconds)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NEXT WALLPAPER CHANGE IN",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timerDisplay,
                            color = ElectricCyan,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Change Duration / Interval Selector
        Text(
            text = "Change Duration Setting:",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(intervals) { item ->
                GlassChip(
                    text = item.second,
                    isSelected = currentSettings.intervalMinutes == item.first,
                    onClick = { onIntervalChange(item.first) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playlist Source Category Selector
        Text(
            text = "Wallpaper Source Category:",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                GlassChip(
                    text = "All Categories",
                    isSelected = currentSettings.selectedCategory == "All",
                    onClick = { onCategoryChange("All") }
                )
            }
            items(categories) { cat ->
                GlassChip(
                    text = cat.name,
                    isSelected = currentSettings.selectedCategory.equals(cat.name, ignoreCase = true),
                    onClick = { onCategoryChange(cat.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Content based on Specific Tags & Resolutions
        Text(
            text = "Filter Content by Tag:",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tagFilterOptions) { tag ->
                GlassChip(
                    text = if (tag == "All") "Any Tag" else "#$tag",
                    isSelected = selectedTagFilter == tag,
                    onClick = { selectedTagFilter = tag }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Filter Content by Resolution:",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(resolutionOptions) { res ->
                GlassChip(
                    text = if (res == "All") "Any Resolution" else res,
                    isSelected = selectedResolutionFilter == res,
                    onClick = { selectedResolutionFilter = res }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trigger Now Button with Notifications & Haptics
        Button(
            onClick = {
                WallpaperHelper.triggerHaptic(haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onTriggerNow()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("trigger_wallpaper_now_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Switch Wallpaper Right Now",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
