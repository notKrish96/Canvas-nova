package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers ORDER BY dateAdded DESC")
    fun getAllWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isNsfw = 0 ORDER BY dateAdded DESC")
    fun getSafeWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isNsfw = 1 ORDER BY dateAdded DESC")
    fun getNsfwWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE category = :category ORDER BY dateAdded DESC")
    fun getWallpapersByCategory(category: String): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE id = :id")
    suspend fun getWallpaperById(id: String): WallpaperEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpapers(wallpapers: List<WallpaperEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpaper(wallpaper: WallpaperEntity)

    @Update
    suspend fun updateWallpaper(wallpaper: WallpaperEntity)

    @Query("DELETE FROM wallpapers WHERE id = :id")
    suspend fun deleteWallpaperById(id: String)

    // Category queries
    @Query("SELECT * FROM categories ORDER BY isCustom DESC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomCategory(id: String)

    // Auto Changer Settings queries
    @Query("SELECT * FROM autochanger_settings WHERE id = 1")
    fun getAutoChangerSettings(): Flow<AutoChangerSettingsEntity?>

    @Query("SELECT * FROM autochanger_settings WHERE id = 1")
    suspend fun getAutoChangerSettingsDirect(): AutoChangerSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAutoChangerSettings(settings: AutoChangerSettingsEntity)

    // Sync Logs queries
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 20")
    fun getSyncLogs(): Flow<List<SyncLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncLog(log: SyncLogEntity)
}
