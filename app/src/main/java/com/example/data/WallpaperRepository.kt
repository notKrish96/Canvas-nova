package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class WallpaperRepository(private val wallpaperDao: WallpaperDao) {

    val allWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getAllWallpapers()
    val safeWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getSafeWallpapers()
    val nsfwWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getNsfwWallpapers()
    val favoriteWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getFavoriteWallpapers()
    val allCategories: Flow<List<CategoryEntity>> = wallpaperDao.getAllCategories()
    val autoChangerSettings: Flow<AutoChangerSettingsEntity?> = wallpaperDao.getAutoChangerSettings()
    val syncLogs: Flow<List<SyncLogEntity>> = wallpaperDao.getSyncLogs()

    suspend fun getWallpaperById(id: String) = wallpaperDao.getWallpaperById(id)

    suspend fun toggleFavorite(id: String) {
        val wallpaper = wallpaperDao.getWallpaperById(id)
        wallpaper?.let {
            wallpaperDao.updateWallpaper(it.copy(isFavorite = !it.isFavorite))
        }
    }

    suspend fun markDownloaded(id: String, localPath: String) {
        val wallpaper = wallpaperDao.getWallpaperById(id)
        wallpaper?.let {
            wallpaperDao.updateWallpaper(it.copy(isDownloaded = true, localFilePath = localPath))
        }
    }

    suspend fun insertWallpaper(wallpaper: WallpaperEntity) {
        wallpaperDao.insertWallpaper(wallpaper)
    }

    suspend fun insertCustomCategory(name: String, iconName: String) {
        val category = CategoryEntity(
            id = "custom_" + System.currentTimeMillis(),
            name = name,
            iconName = iconName,
            count = 0,
            isCustom = true
        )
        wallpaperDao.insertCategory(category)
    }

    suspend fun deleteCustomCategory(id: String) {
        wallpaperDao.deleteCustomCategory(id)
    }

    suspend fun updateAutoChangerSettings(settings: AutoChangerSettingsEntity) {
        wallpaperDao.updateAutoChangerSettings(settings)
    }

    suspend fun getAutoChangerSettingsDirect() = wallpaperDao.getAutoChangerSettingsDirect()

    suspend fun addSyncLog(action: String, itemsSynced: Int, status: String) {
        wallpaperDao.insertSyncLog(SyncLogEntity(action = action, itemsSynced = itemsSynced, status = status))
    }

    suspend fun seedInitialDataIfEmpty() {
        val initialCategories = listOf(
            CategoryEntity("glass_amoled", "Glass & AMOLED", "AutoAwesome", 6, false, "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80"),
            CategoryEntity("cyberpunk", "Cyberpunk & Neon", "ElectricBolt", 8, false, "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&q=80"),
            CategoryEntity("space", "Deep Space", "Public", 5, false, "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&q=80"),
            CategoryEntity("anime", "Anime & Studio", "Palette", 5, false, "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&q=80"),
            CategoryEntity("minimalist", "Minimalist Art", "CropSquare", 6, false, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80"),
            CategoryEntity("nature", "Nature & Wild", "FilterVintage", 5, false, "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&q=80"),
            CategoryEntity("ai_art", "AI Masterpieces", "Psychology", 6, false, "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=800&q=80"),
            CategoryEntity("nsfw_vault", "Restricted 18+", "Lock", 5, false, "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=800&q=80")
        )
        wallpaperDao.insertCategories(initialCategories)

        val initialWallpapers = listOf(
            // Glass & AMOLED
            WallpaperEntity(
                id = "wp_1",
                title = "Frosted Prism Waves",
                category = "Glass & AMOLED",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=2400&q=90",
                tags = "dark,glass,oled,4k,abstract",
                resolution = "4K (3840x2160)",
                isNsfw = false,
                isFavorite = true
            ),
            WallpaperEntity(
                id = "wp_2",
                title = "Holographic Sphere",
                category = "Glass & AMOLED",
                imageUrl = "https://images.unsplash.com/photo-1614850523459-c2f4c699c52e?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1614850523459-c2f4c699c52e?w=2400&q=90",
                tags = "glass,3d,cyan,oled,1440p",
                resolution = "1440p",
                isNsfw = false
            ),
            WallpaperEntity(
                id = "wp_3",
                title = "Golden Silk Noir",
                category = "Glass & AMOLED",
                imageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=2400&q=90",
                tags = "gold,dark,oled,4k,lux",
                resolution = "4K (3840x2160)",
                isNsfw = false
            ),

            // Cyberpunk & Neon
            WallpaperEntity(
                id = "wp_4",
                title = "Neon Rain Horizon",
                category = "Cyberpunk & Neon",
                imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=2400&q=90",
                tags = "neon,cyberpunk,city,4k,dark",
                resolution = "4K (3840x2160)",
                isNsfw = false,
                isFavorite = true
            ),
            WallpaperEntity(
                id = "wp_5",
                title = "Retro Synthwave Grid",
                category = "Cyberpunk & Neon",
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=2400&q=90",
                tags = "synthwave,neon,retro,1080p,grid",
                resolution = "1080p",
                isNsfw = false
            ),
            WallpaperEntity(
                id = "wp_6",
                title = "Neon Game Nexus",
                category = "Cyberpunk & Neon",
                imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=2400&q=90",
                tags = "gaming,neon,magenta,1440p",
                resolution = "1440p",
                isNsfw = false
            ),

            // Deep Space
            WallpaperEntity(
                id = "wp_7",
                title = "Cosmic Dust Nebula",
                category = "Deep Space",
                imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=2400&q=90",
                tags = "space,galaxy,stars,4k,dark",
                resolution = "4K (3840x2160)",
                isNsfw = false,
                isFavorite = true
            ),
            WallpaperEntity(
                id = "wp_8",
                title = "Starry Peak Aurora",
                category = "Deep Space",
                imageUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=2400&q=90",
                tags = "stars,mountains,night,1440p",
                resolution = "1440p",
                isNsfw = false
            ),

            // Minimalist Art
            WallpaperEntity(
                id = "wp_9",
                title = "Dark Geometry Minimal",
                category = "Minimalist Art",
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=2400&q=90",
                tags = "minimal,dark,geometry,oled,4k",
                resolution = "4K (3840x2160)",
                isNsfw = false
            ),
            WallpaperEntity(
                id = "wp_10",
                title = "Prism Light Gradient",
                category = "Minimalist Art",
                imageUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=2400&q=90",
                tags = "gradient,minimal,clean,1080p",
                resolution = "1080p",
                isNsfw = false
            ),

            // AI Masterpieces
            WallpaperEntity(
                id = "wp_11",
                title = "Fluid Chromatic Motion",
                category = "AI Masterpieces",
                imageUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=2400&q=90",
                tags = "ai,fluid,color,3d,4k",
                resolution = "4K (3840x2160)",
                isNsfw = false
            ),

            // Restricted 18+ (Vault)
            WallpaperEntity(
                id = "wp_18_1",
                title = "Sensual Silhouette Noir",
                category = "Restricted 18+",
                imageUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=2400&q=90",
                tags = "18+,artistic,silhouette,noir,portrait",
                resolution = "4K (3840x2160)",
                isNsfw = true
            ),
            WallpaperEntity(
                id = "wp_18_2",
                title = "Crimson Studio Glow",
                category = "Restricted 18+",
                imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=2400&q=90",
                tags = "18+,crimson,portrait,glamour,1440p",
                resolution = "1440p",
                isNsfw = true
            ),
            WallpaperEntity(
                id = "wp_18_3",
                title = "Moody Noir Contour",
                category = "Restricted 18+",
                imageUrl = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=1400&q=80",
                hdUrl = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=2400&q=90",
                tags = "18+,erotic,dark,shadow,4k",
                resolution = "4K (3840x2160)",
                isNsfw = true
            )
        )
        wallpaperDao.insertWallpapers(initialWallpapers)

        // Default AutoChanger settings (5 minutes)
        if (wallpaperDao.getAutoChangerSettingsDirect() == null) {
            wallpaperDao.updateAutoChangerSettings(
                AutoChangerSettingsEntity(
                    isEnabled = true,
                    intervalMinutes = 5,
                    selectedCategory = "All",
                    selectedTagFilter = "All",
                    selectedResolutionFilter = "All",
                    lastChangedTimestamp = System.currentTimeMillis()
                )
            )
        }

        wallpaperDao.insertSyncLog(
            SyncLogEntity(
                action = "Initial Local Cache Provisioning",
                itemsSynced = initialWallpapers.size,
                status = "SUCCESS"
            )
        )
    }
}
