package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.TextSecondary
import com.example.utils.WallpaperHelper

enum class NavTab(val title: String, val icon: ImageVector) {
    EXPLORE("Explore", Icons.Default.Explore),
    AI_GEN("AI Art", Icons.Default.AutoAwesome),
    AUTO_CHANGER("Auto", Icons.Default.Schedule),
    VAULT_18("18+ Vault", Icons.Default.Lock),
    CLOUD_SYNC("Cloud", Icons.Default.CloudSync)
}

@Composable
fun GlassNavigationBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x26FFFFFF),
                            Color(0x0DFFFFFF)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x33FFFFFF),
                            Color(0x0AFFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(34.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val animatedTint by animateColorAsState(
                        targetValue = if (isSelected) Color.White else TextSecondary,
                        label = "tabTint"
                    )

                    Column(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                WallpaperHelper.triggerHaptic(haptic)
                                onTabSelected(tab)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                        } else {
                            Spacer(modifier = Modifier.height(7.dp))
                        }

                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = animatedTint,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.title,
                            color = animatedTint,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

