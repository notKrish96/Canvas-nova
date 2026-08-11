package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.WallpaperEntity
import com.example.ui.components.GlassNavigationBar
import com.example.ui.components.NavTab
import com.example.ui.dialogs.WallpaperDetailDialog
import com.example.ui.screens.*
import com.example.ui.theme.AmbientBlueGlow
import com.example.ui.theme.AmbientPurpleGlow
import com.example.ui.theme.CanvasNovaTheme
import com.example.ui.theme.DarkCanvas
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasNovaTheme {
                CanvasNovaApp()
            }
        }
    }
}

@Composable
fun CanvasNovaApp(viewModel: MainViewModel = viewModel()) {
    val haptic = LocalHapticFeedback.current
    var currentTab by remember { mutableStateOf(NavTab.EXPLORE) }
    var selectedWallpaperForDetail by remember { mutableStateOf<WallpaperEntity?>(null) }

    // State collections
    val wallpapers by viewModel.filteredWallpapers.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTagFilter by viewModel.selectedTagFilter.collectAsStateWithLifecycle()
    val selectedResolutionFilter by viewModel.selectedResolutionFilter.collectAsStateWithLifecycle()

    val autoChangerSettings by viewModel.autoChangerSettings.collectAsStateWithLifecycle()
    val nextChangeSecondsLeft by viewModel.nextChangeSecondsLeft.collectAsStateWithLifecycle()

    val ageState by viewModel.ageVerificationState.collectAsStateWithLifecycle()
    val nsfwWallpapers by viewModel.nsfwWallpapers.collectAsStateWithLifecycle()

    val cloudState by viewModel.cloudSyncState.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        // Atmospheric Ambient Soft Glow Canvas (Frosted Glass backdrop glows)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top-Left Soft Blue Ambient Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AmbientBlueGlow, Color.Transparent),
                    center = Offset(x = width * 0.15f, y = height * 0.15f),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(x = width * 0.15f, y = height * 0.15f)
            )

            // Bottom-Right Soft Purple Ambient Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AmbientPurpleGlow, Color.Transparent),
                    center = Offset(x = width * 0.85f, y = height * 0.85f),
                    radius = width * 0.85f
                ),
                radius = width * 0.85f,
                center = Offset(x = width * 0.85f, y = height * 0.85f)
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                GlassNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> currentTab = tab }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
                    when (tab) {
                        NavTab.EXPLORE -> {
                            ExploreScreen(
                                wallpapers = wallpapers,
                                categories = categories,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                selectedTagFilter = selectedTagFilter,
                                selectedResolutionFilter = selectedResolutionFilter,
                                onCategorySelect = { viewModel.setSelectedCategory(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onTagSelect = { viewModel.setSelectedTagFilter(it) },
                                onResolutionSelect = { viewModel.setSelectedResolutionFilter(it) },
                                onWallpaperClick = { wp -> selectedWallpaperForDetail = wp },
                                onFavoriteToggle = { id -> viewModel.toggleFavorite(id, haptic) },
                                onApplyQuick = { wp -> viewModel.applyWallpaper(wp, "BOTH", haptic) },
                                onAddCategoryClick = { currentTab = NavTab.CLOUD_SYNC }
                            )
                        }

                        NavTab.AI_GEN -> {
                            AiGeneratorScreen(
                                onGenerate = { prompt, style, res, ratio, callback ->
                                    viewModel.generateAiWallpaper(prompt, style, res, ratio, callback)
                                },
                                onApplyWallpaper = { wp -> viewModel.applyWallpaper(wp, "BOTH", haptic) },
                                onDownloadWallpaper = { wp -> viewModel.downloadWallpaper(wp, haptic) }
                            )
                        }

                        NavTab.AUTO_CHANGER -> {
                            AutoChangerScreen(
                                settings = autoChangerSettings,
                                categories = categories,
                                nextChangeSecondsLeft = nextChangeSecondsLeft,
                                onIntervalChange = { mins -> viewModel.updateAutoChangerInterval(mins) },
                                onCategoryChange = { cat -> viewModel.updateAutoChangerCategory(cat) },
                                onToggleEnabled = { enabled -> viewModel.toggleAutoChangerEnabled(enabled) },
                                onTriggerNow = { viewModel.triggerAutoChangeNow(haptic) }
                            )
                        }

                        NavTab.VAULT_18 -> {
                            VaultScreen(
                                ageState = ageState,
                                nsfwWallpapers = nsfwWallpapers,
                                onVerifyGoogleOAuth = { viewModel.verifyAgeViaGoogleOAuth() },
                                onVerifyIdUpload = { uri -> viewModel.verifyAgeViaIdUpload(uri) },
                                onRevokeVerification = { viewModel.revokeAgeVerification() },
                                onWallpaperClick = { wp -> selectedWallpaperForDetail = wp },
                                onFavoriteToggle = { id -> viewModel.toggleFavorite(id, haptic) },
                                onApplyQuick = { wp -> viewModel.applyWallpaper(wp, "BOTH", haptic) }
                            )
                        }

                        NavTab.CLOUD_SYNC -> {
                            CloudSyncScreen(
                                syncState = cloudState,
                                categories = categories,
                                syncLogs = syncLogs,
                                onPerformBackup = { viewModel.performCloudBackup() },
                                onAddCategory = { name -> viewModel.addCustomCategory(name) },
                                onDeleteCustomCategory = { id -> viewModel.deleteCustomCategory(id) }
                            )
                        }
                    }
                }

                // Wallpaper Detail Modal
                selectedWallpaperForDetail?.let { wp ->
                    WallpaperDetailDialog(
                        wallpaper = wp,
                        onDismiss = { selectedWallpaperForDetail = null },
                        onApply = { screen ->
                            viewModel.applyWallpaper(wp, screen, haptic)
                            selectedWallpaperForDetail = null
                        },
                        onDownload = {
                            viewModel.downloadWallpaper(wp, haptic)
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavorite(wp.id, haptic)
                            selectedWallpaperForDetail = selectedWallpaperForDetail?.copy(isFavorite = !wp.isFavorite)
                        }
                    )
                }
            }
        }
    }
}

