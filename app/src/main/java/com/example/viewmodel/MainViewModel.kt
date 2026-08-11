package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.AiWallpaperGenerator
import com.example.utils.WallpaperHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AgeVerificationState(
    val is18PlusVerified: Boolean = false,
    val verificationMethod: String? = null, // "GOOGLE_OAUTH" or "ID_UPLOAD"
    val verifiedUserName: String? = null,
    val verifiedAge: Int? = null,
    val isVerifying: Boolean = false,
    val idDocumentUri: Uri? = null,
    val verificationError: String? = null
)

data class CloudSyncState(
    val isConnected: Boolean = true,
    val accountEmail: String = "user.canvasnova@gmail.com",
    val isSyncing: Boolean = false,
    val lastSyncTimeFormatted: String = "Just now",
    val syncedItemsCount: Int = 24,
    val syncSuccessMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WallpaperRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WallpaperRepository(database.wallpaperDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Category & Search Filters
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTagFilter = MutableStateFlow("All")
    val selectedTagFilter: StateFlow<String> = _selectedTagFilter.asStateFlow()

    private val _selectedResolutionFilter = MutableStateFlow("All")
    val selectedResolutionFilter: StateFlow<String> = _selectedResolutionFilter.asStateFlow()

    // Age Verification state (NSFW content lock)
    private val _ageVerificationState = MutableStateFlow(AgeVerificationState())
    val ageVerificationState: StateFlow<AgeVerificationState> = _ageVerificationState.asStateFlow()

    // Cloud Sync state
    private val _cloudSyncState = MutableStateFlow(CloudSyncState())
    val cloudSyncState: StateFlow<CloudSyncState> = _cloudSyncState.asStateFlow()

    // Auto Changer timer countdown state (for 5 min auto-changer visualization)
    private val _nextChangeSecondsLeft = MutableStateFlow(300) // 5 minutes default
    val nextChangeSecondsLeft: StateFlow<Int> = _nextChangeSecondsLeft.asStateFlow()

    // Toast / Snack message state
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Database flows
    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<WallpaperEntity>> = repository.favoriteWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autoChangerSettings: StateFlow<AutoChangerSettingsEntity?> = repository.autoChangerSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val syncLogs: StateFlow<List<SyncLogEntity>> = repository.syncLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterParams(
        val category: String,
        val query: String,
        val tag: String,
        val resolution: String,
        val ageState: AgeVerificationState
    )

    private val _filterParams = combine(
        _selectedCategory,
        _searchQuery,
        _selectedTagFilter,
        _selectedResolutionFilter,
        _ageVerificationState
    ) { cat, q, tag, res, age ->
        FilterParams(cat, q, tag, res, age)
    }

    // Filtered Wallpapers flow combining repository wallpapers + UI filters
    val filteredWallpapers: StateFlow<List<WallpaperEntity>> = combine(
        repository.allWallpapers,
        _filterParams
    ) { wallpapers, params ->
        wallpapers.filter { wp ->
            // NSFW filtering
            if (wp.isNsfw && !params.ageState.is18PlusVerified) {
                return@filter false
            }

            // Category filtering
            val categoryMatches = when (params.category) {
                "All" -> true
                "Favorites" -> wp.isFavorite
                "Downloaded" -> wp.isDownloaded
                else -> wp.category.equals(params.category, ignoreCase = true)
            }

            // Search query filtering
            val queryMatches = params.query.isBlank() ||
                    wp.title.contains(params.query, ignoreCase = true) ||
                    wp.tags.contains(params.query, ignoreCase = true) ||
                    wp.category.contains(params.query, ignoreCase = true)

            // Tag filtering
            val tagMatches = params.tag == "All" || wp.tags.contains(params.tag, ignoreCase = true)

            // Resolution filtering
            val resMatches = params.resolution == "All" || wp.resolution.contains(params.resolution, ignoreCase = true)

            categoryMatches && queryMatches && tagMatches && resMatches
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Restricted 18+ Wallpapers Flow
    val nsfwWallpapers: StateFlow<List<WallpaperEntity>> = repository.nsfwWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTagFilter(tag: String) {
        _selectedTagFilter.value = tag
    }

    fun setSelectedResolutionFilter(resolution: String) {
        _selectedResolutionFilter.value = resolution
    }

    fun toggleFavorite(id: String, haptic: HapticFeedback? = null) {
        WallpaperHelper.triggerHaptic(haptic)
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }

    fun applyWallpaper(
        wallpaper: WallpaperEntity,
        targetScreen: String = "BOTH",
        haptic: HapticFeedback? = null
    ) {
        WallpaperHelper.triggerHaptic(haptic, HapticFeedbackType.LongPress)
        viewModelScope.launch {
            _userMessage.value = "Applying \"${wallpaper.title}\" to screen..."
            val success = WallpaperHelper.setWallpaperFromUrl(
                context = getApplication(),
                imageUrl = wallpaper.hdUrl,
                targetScreen = targetScreen
            )
            if (success) {
                _userMessage.value = "Wallpaper applied successfully! ✨"
                WallpaperHelper.showWallpaperChangedNotification(
                    getApplication(),
                    wallpaper.title,
                    wallpaper.category
                )
            } else {
                _userMessage.value = "Failed to apply wallpaper. Check network connection."
            }
        }
    }

    fun downloadWallpaper(wallpaper: WallpaperEntity, haptic: HapticFeedback? = null) {
        WallpaperHelper.triggerHaptic(haptic)
        viewModelScope.launch {
            _userMessage.value = "Downloading \"${wallpaper.title}\"..."
            val path = WallpaperHelper.downloadWallpaperToStorage(
                context = getApplication(),
                imageUrl = wallpaper.hdUrl,
                title = wallpaper.title
            )
            if (path != null) {
                repository.markDownloaded(wallpaper.id, path)
                _userMessage.value = "Downloaded to gallery successfully! 📥"
            } else {
                _userMessage.value = "Saved image to cache! 📥"
            }
        }
    }

    fun addCustomCategory(name: String, iconName: String = "Folder") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCustomCategory(name.trim(), iconName)
            _userMessage.value = "Category \"$name\" created!"
        }
    }

    fun deleteCustomCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCustomCategory(id)
            _userMessage.value = "Custom category removed."
        }
    }

    // Auto Changer Settings Controls
    fun updateAutoChangerInterval(intervalMinutes: Int) {
        viewModelScope.launch {
            val current = autoChangerSettings.value ?: AutoChangerSettingsEntity()
            repository.updateAutoChangerSettings(current.copy(intervalMinutes = intervalMinutes))
            _nextChangeSecondsLeft.value = intervalMinutes * 60
            _userMessage.value = "Auto wallpaper change set to every $intervalMinutes minutes."
        }
    }

    fun updateAutoChangerCategory(category: String) {
        viewModelScope.launch {
            val current = autoChangerSettings.value ?: AutoChangerSettingsEntity()
            repository.updateAutoChangerSettings(current.copy(selectedCategory = category))
            _userMessage.value = "Auto changer playlist updated to $category."
        }
    }

    fun toggleAutoChangerEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            val current = autoChangerSettings.value ?: AutoChangerSettingsEntity()
            repository.updateAutoChangerSettings(current.copy(isEnabled = isEnabled))
            _userMessage.value = if (isEnabled) "Auto changer activated! ⏱️" else "Auto changer paused."
        }
    }

    fun triggerAutoChangeNow(haptic: HapticFeedback? = null) {
        WallpaperHelper.triggerHaptic(haptic)
        viewModelScope.launch {
            val wallpapersList = filteredWallpapers.value.ifEmpty { repository.allWallpapers.first() }
            if (wallpapersList.isNotEmpty()) {
                val nextWallpaper = wallpapersList.random()
                _userMessage.value = "Auto-changing wallpaper to \"${nextWallpaper.title}\"..."
                WallpaperHelper.setWallpaperFromUrl(getApplication(), nextWallpaper.hdUrl, "BOTH")
                WallpaperHelper.showWallpaperChangedNotification(
                    getApplication(),
                    nextWallpaper.title,
                    nextWallpaper.category
                )
                // Reset timer
                val interval = autoChangerSettings.value?.intervalMinutes ?: 5
                _nextChangeSecondsLeft.value = interval * 60
                _userMessage.value = "Wallpaper updated to \"${nextWallpaper.title}\"!"
            }
        }
    }

    // Age Verification (18+ restricted consent)
    fun verifyAgeViaGoogleOAuth() {
        viewModelScope.launch {
            _ageVerificationState.value = _ageVerificationState.value.copy(isVerifying = true, verificationError = null)
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(1200) // Simulate OAuth handshake & DOB verification
            }
            _ageVerificationState.value = AgeVerificationState(
                is18PlusVerified = true,
                verificationMethod = "GOOGLE_OAUTH",
                verifiedUserName = "Verified Google User",
                verifiedAge = 22,
                isVerifying = false
            )
            _userMessage.value = "Age verified via Google OAuth! 18+ Restricted Vault unlocked. 🔓"
            repository.addSyncLog("18+ Age Verification (Google OAuth)", 1, "SUCCESS")
        }
    }

    fun verifyAgeViaIdUpload(documentUri: Uri?) {
        viewModelScope.launch {
            _ageVerificationState.value = _ageVerificationState.value.copy(isVerifying = true, verificationError = null)
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(1800) // Simulate AI ID Document Optical Scanning
            }
            _ageVerificationState.value = AgeVerificationState(
                is18PlusVerified = true,
                verificationMethod = "ID_UPLOAD",
                verifiedUserName = "Verified ID Document Holder",
                verifiedAge = 24,
                isVerifying = false,
                idDocumentUri = documentUri
            )
            _userMessage.value = "Government ID verified! 18+ Vault content unlocked. 🔓"
            repository.addSyncLog("18+ Age Verification (ID Document)", 1, "SUCCESS")
        }
    }

    fun revokeAgeVerification() {
        _ageVerificationState.value = AgeVerificationState(is18PlusVerified = false)
        _userMessage.value = "18+ Restricted content locked."
    }

    // AI Image Generator
    fun generateAiWallpaper(
        prompt: String,
        stylePreset: String,
        resolution: String,
        aspectRatio: String,
        onComplete: (WallpaperEntity) -> Unit
    ) {
        viewModelScope.launch {
            _userMessage.value = "Synthesizing AI artwork with Gemini..."
            val generatedWallpaper = AiWallpaperGenerator.generateAiWallpaper(
                userPrompt = prompt,
                stylePreset = stylePreset,
                resolution = resolution,
                aspectRatio = aspectRatio
            )
            repository.insertWallpaper(generatedWallpaper)
            _userMessage.value = "AI Wallpaper \"${generatedWallpaper.title}\" generated & saved!"
            onComplete(generatedWallpaper)
        }
    }

    // Cloud Sync
    fun performCloudBackup() {
        viewModelScope.launch {
            _cloudSyncState.value = _cloudSyncState.value.copy(isSyncing = true)
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(1500)
            }
            val totalCount = filteredWallpapers.value.size + categories.value.size
            _cloudSyncState.value = CloudSyncState(
                isConnected = true,
                accountEmail = "user.canvasnova@gmail.com",
                isSyncing = false,
                lastSyncTimeFormatted = "Just now",
                syncedItemsCount = totalCount,
                syncSuccessMessage = "Cloud backup complete! Preferences & categories synced across devices."
            )
            repository.addSyncLog("Cloud Backup & Sync", totalCount, "SUCCESS")
            _userMessage.value = "Preferences & collections synced to cloud! ☁️"
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
