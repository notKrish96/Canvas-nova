package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
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
import com.example.data.SyncLogEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.utils.WallpaperHelper
import com.example.viewmodel.CloudSyncState

@Composable
fun CloudSyncScreen(
    syncState: CloudSyncState,
    categories: List<CategoryEntity>,
    syncLogs: List<SyncLogEntity>,
    onPerformBackup: () -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCustomCategory: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showAddCategoryModal by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

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
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "CLOUD SYNC & SETTINGS",
                    color = ElectricCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Sync preferences & categories across all devices",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cloud Backup Status Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ElectricCyan
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.2f))
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = ElectricCyan)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = syncState.accountEmail,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last Synced: ${syncState.lastSyncTimeFormatted} (${syncState.syncedItemsCount} Items)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    WallpaperHelper.triggerHaptic(haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onPerformBackup()
                },
                enabled = !syncState.isSyncing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("backup_preferences_now_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (syncState.isSyncing) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Syncing with Canvas Cloud...", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backup Preferences & Collections Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom Categories Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom Categories:",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    WallpaperHelper.triggerHaptic(haptic)
                    showAddCategoryModal = true
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GlassCardBg)
                    .border(1.dp, GlassCardBorder, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricCyan)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val customCategories = categories.filter { it.isCustom }
        if (customCategories.isEmpty()) {
            Text(
                text = "No custom categories created yet. Tap '+' to add your own custom folder.",
                color = TextMuted,
                fontSize = 12.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                customCategories.forEach { cat ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                            IconButton(
                                onClick = { onDeleteCustomCategory(cat.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NsfwWarningColor)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Sync Activity History
        Text(
            text = "Cloud Sync Logs:",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            syncLogs.take(5).forEach { log ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(log.action, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${log.itemsSynced} items • ${log.status}", color = TextSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.History, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // Add Category Modal
    if (showAddCategoryModal) {
        AlertDialog(
            onDismissRequest = { showAddCategoryModal = false },
            title = { Text("Add Custom Category", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("e.g. Minimal AMOLED, Anime Vibe") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName)
                            newCategoryName = ""
                            showAddCategoryModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Create", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
