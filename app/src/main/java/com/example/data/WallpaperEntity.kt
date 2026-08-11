package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val hdUrl: String,
    val tags: String, // comma separated tags e.g. "dark,4k,oled,nature"
    val resolution: String, // e.g. "4K (3840x2160)", "1440p", "1080p"
    val isNsfw: Boolean = false,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val dateAdded: Long = System.currentTimeMillis()
)
