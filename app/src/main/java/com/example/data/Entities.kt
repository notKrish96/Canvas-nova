package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val count: Int = 0,
    val isCustom: Boolean = false,
    val coverImageUrl: String = ""
)

@Entity(tableName = "autochanger_settings")
data class AutoChangerSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean = true,
    val intervalMinutes: Int = 5, // Default 5 minutes as requested
    val selectedCategory: String = "All", // "All" or specific category
    val selectedTagFilter: String = "All",
    val selectedResolutionFilter: String = "All",
    val lastChangedTimestamp: Long = 0L,
    val targetScreen: String = "BOTH" // "HOME", "LOCK", "BOTH"
)

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String, // e.g. "Cloud Backup", "Cloud Restore", "Preferences Synced"
    val itemsSynced: Int,
    val status: String // "SUCCESS", "PENDING", "FAILED"
)
