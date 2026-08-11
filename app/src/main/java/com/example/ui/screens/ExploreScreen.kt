package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryEntity
import com.example.data.WallpaperEntity
import com.example.ui.components.GlassChip
import com.example.ui.components.WallpaperItemCard
import com.example.ui.theme.*
import com.example.utils.WallpaperHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    wallpapers: List<WallpaperEntity>,
    categories: List<CategoryEntity>,
    selectedCategory: String,
    searchQuery: String,
    selectedTagFilter: String,
    selectedResolutionFilter: String,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onResolutionSelect: (String) -> Unit,
    onWallpaperClick: (WallpaperEntity) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onApplyQuick: (WallpaperEntity) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val tagOptions = listOf("All", "dark", "oled", "4k", "glass", "neon", "space", "minimal")
    val resOptions = listOf("All", "4K (3840x2160)", "1440p", "1080p")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CANVAS NOVA",
                    color = ElectricCyan,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Discover High-Res Wallpapers",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = {
                    WallpaperHelper.triggerHaptic(haptic)
                    onAddCategoryClick()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlassCardBg)
                    .border(1.dp, GlassCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = ElectricCyan
                )
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by tag, color, or style...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricCyan) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .testTag("search_wallpaper_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                GlassChip(
                    text = "All",
                    isSelected = selectedCategory == "All",
                    onClick = { onCategorySelect("All") }
                )
            }
            item {
                GlassChip(
                    text = "❤️ Favorites",
                    isSelected = selectedCategory == "Favorites",
                    onClick = { onCategorySelect("Favorites") }
                )
            }
            item {
                GlassChip(
                    text = "📥 Downloaded",
                    isSelected = selectedCategory == "Downloaded",
                    onClick = { onCategorySelect("Downloaded") }
                )
            }

            items(categories) { category ->
                GlassChip(
                    text = category.name,
                    isSelected = selectedCategory.equals(category.name, ignoreCase = true),
                    onClick = { onCategorySelect(category.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tags & Resolution Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tagOptions) { tag ->
                    GlassChip(
                        text = if (tag == "All") "Tag: All" else "#$tag",
                        isSelected = selectedTagFilter == tag,
                        onClick = { onTagSelect(tag) }
                    )
                }

                items(resOptions) { res ->
                    GlassChip(
                        text = if (res == "All") "Res: All" else res,
                        isSelected = selectedResolutionFilter == res,
                        onClick = { onResolutionSelect(res) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Wallpapers Vertical Grid
        if (wallpapers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No Wallpapers Found",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing search filters or changing category.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wallpapers, key = { it.id }) { wp ->
                    WallpaperItemCard(
                        wallpaper = wp,
                        onClick = { onWallpaperClick(wp) },
                        onFavoriteToggle = { onFavoriteToggle(wp.id) },
                        onApplyQuick = { onApplyQuick(wp) }
                    )
                }
            }
        }
    }
}
