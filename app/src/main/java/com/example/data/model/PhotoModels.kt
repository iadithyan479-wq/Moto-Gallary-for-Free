package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoItem(
    @PrimaryKey val id: String,
    val title: String,
    val uri: String, // Resource name or URI
    val timestamp: Long,
    val dateFormatted: String,
    val location: String,
    val width: Int,
    val height: Int,
    val fileSizeFormatted: String,
    val isFavorite: Boolean = false,
    val isVaulted: Boolean = false,
    val isCloudSynced: Boolean = false,
    val cloudSyncStatus: String = "SYNCED", // SYNCED, PENDING, LOCAL_ONLY
    val cameraModel: String = "Motorola Edge 50 Ultra",
    val aperture: String = "f/1.6",
    val iso: String = "ISO 100",
    val shutterSpeed: String = "1/500s",
    val focalLength: String = "24mm f/1.6 (50MP OmniVision)",
    val tags: String = "monochrome, portrait",
    val faceNames: String = "", // Comma-separated detected person names
    val category: String = "Camera" // Camera, Night Vision, Portraits, Street, Motorola Razr
)

@Entity(tableName = "face_clusters")
data class FaceCluster(
    @PrimaryKey val id: String,
    val personName: String,
    val photoCount: Int,
    val confidence: Float,
    val coverPhotoId: String,
    val coverColorHex: Long = 0xFF27272A
)

@Entity(tableName = "albums")
data class AlbumItem(
    @PrimaryKey val id: String,
    val title: String,
    val photoCount: Int,
    val coverPhotoId: String,
    val isSystemAlbum: Boolean = false
)

data class PairedDevice(
    val id: String,
    val name: String,
    val type: String, // "phone", "tablet", "pc_ready_for", "cloud"
    val lastSyncFormatted: String,
    val isConnected: Boolean,
    val isE2EEncrypted: Boolean = true
)

enum class BackupRule {
    WIFI_AND_CHARGING, // Maximum battery preservation
    WIFI_ONLY,
    ALWAYS
}
