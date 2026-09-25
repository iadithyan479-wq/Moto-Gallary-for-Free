package com.example.data.repository

import android.content.Context
import com.example.data.local.PhotoDao
import com.example.data.model.AlbumItem
import com.example.data.model.BackupRule
import com.example.data.model.FaceCluster
import com.example.data.model.PairedDevice
import com.example.data.model.PhotoItem
import com.example.ui.components.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryRepository(
    private val context: Context,
    private val photoDao: PhotoDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val publicPhotos: Flow<List<PhotoItem>> = photoDao.getPublicPhotos()
    val vaultPhotos: Flow<List<PhotoItem>> = photoDao.getVaultPhotos()
    val favoritePhotos: Flow<List<PhotoItem>> = photoDao.getFavoritePhotos()
    val faceClusters: Flow<List<FaceCluster>> = photoDao.getAllFaceClusters()
    val albums: Flow<List<AlbumItem>> = photoDao.getAllAlbums()

    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _vaultPin = MutableStateFlow("2026") // default secure PIN
    val vaultPin: StateFlow<String> = _vaultPin.asStateFlow()

    private val _failedUnlockAttempts = MutableStateFlow(0)
    val failedUnlockAttempts: StateFlow<Int> = _failedUnlockAttempts.asStateFlow()

    private val _backupRule = MutableStateFlow(BackupRule.WIFI_AND_CHARGING)
    val backupRule: StateFlow<BackupRule> = _backupRule.asStateFlow()

    private val _isBackingUp = MutableStateFlow(false)
    val isBackingUp: StateFlow<Boolean> = _isBackingUp.asStateFlow()

    private val _backupProgress = MutableStateFlow(1.0f)
    val backupProgress: StateFlow<Float> = _backupProgress.asStateFlow()

    private val _pairedDevices = MutableStateFlow(
        listOf(
            PairedDevice("dev_1", "Motorola ThinkPhone 25", "phone", "Today, 1:45 PM", isConnected = true),
            PairedDevice("dev_2", "Moto Ready For (Workstation PC)", "pc_ready_for", "Yesterday, 6:12 PM", isConnected = true),
            PairedDevice("dev_3", "Motorola Tab G70", "tablet", "Sep 22, 2026", isConnected = false),
            PairedDevice("dev_4", "E2E Encrypted Cloud Vault", "cloud", "10 minutes ago", isConnected = true)
        )
    )
    val pairedDevices: StateFlow<List<PairedDevice>> = _pairedDevices.asStateFlow()

    init {
        scope.launch {
            checkAndSeedInitialData()
        }
    }

    private suspend fun checkAndSeedInitialData() {
        val count = photoDao.getPhotoCount()
        if (count == 0) {
            val initialPhotos = listOf(
                PhotoItem(
                    id = "photo_1",
                    title = "Monochrome Architecture",
                    uri = "asset_arch_1",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30, // 30 mins ago
                    dateFormatted = "Today, 1:22 PM",
                    location = "Chicago, IL (Motorola HQ)",
                    width = 4096,
                    height = 3072,
                    fileSizeFormatted = "8.4 MB",
                    isFavorite = true,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola Edge 50 Ultra",
                    aperture = "f/1.6",
                    iso = "ISO 50",
                    shutterSpeed = "1/1000s",
                    focalLength = "24mm f/1.6 (50MP OmniVision)",
                    tags = "architecture, geometry, minimalist, contrast",
                    faceNames = "",
                    category = "Camera"
                ),
                PhotoItem(
                    id = "photo_2",
                    title = "Studio Portrait - Elena",
                    uri = "asset_portrait_elena",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3, // 3 hours ago
                    dateFormatted = "Today, 10:45 AM",
                    location = "Austin Studio",
                    width = 4096,
                    height = 5120,
                    fileSizeFormatted = "12.1 MB",
                    isFavorite = true,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola Edge 50 Ultra (3x Portrait)",
                    aperture = "f/2.4",
                    iso = "ISO 100",
                    shutterSpeed = "1/250s",
                    focalLength = "72mm Periscope (64MP)",
                    tags = "portrait, monochrome, studio, lighting",
                    faceNames = "Elena Rostova",
                    category = "Portraits"
                ),
                PhotoItem(
                    id = "photo_3",
                    title = "Motorola Night Vision Cityscape",
                    uri = "asset_night_city",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 20, // Yesterday
                    dateFormatted = "Yesterday, 9:15 PM",
                    location = "Tokyo Skyline",
                    width = 4096,
                    height = 2304,
                    fileSizeFormatted = "9.7 MB",
                    isFavorite = false,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola ThinkPhone 25",
                    aperture = "f/1.8",
                    iso = "ISO 1600",
                    shutterSpeed = "1/8s OIS",
                    focalLength = "26mm f/1.8 (Moto Night Mode)",
                    tags = "night vision, neon, contrast, cityscape",
                    faceNames = "",
                    category = "Night Vision"
                ),
                PhotoItem(
                    id = "photo_4",
                    title = "Marcus by the River",
                    uri = "asset_portrait_marcus",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 28,
                    dateFormatted = "Yesterday, 1:10 PM",
                    location = "Riverwalk",
                    width = 3840,
                    height = 2880,
                    fileSizeFormatted = "7.8 MB",
                    isFavorite = true,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola Razr 50 Ultra",
                    aperture = "f/1.7",
                    iso = "ISO 125",
                    shutterSpeed = "1/500s",
                    focalLength = "24mm f/1.7",
                    tags = "portrait, outdoor, natural light",
                    faceNames = "Marcus Vance",
                    category = "Portraits"
                ),
                PhotoItem(
                    id = "photo_5",
                    title = "Sophia Chen - Natural Smile",
                    uri = "asset_portrait_sophia",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
                    dateFormatted = "Sep 23, 2026",
                    location = "Botanical Greenhouse",
                    width = 4096,
                    height = 4096,
                    fileSizeFormatted = "10.2 MB",
                    isFavorite = false,
                    isVaulted = false,
                    isCloudSynced = false,
                    cloudSyncStatus = "PENDING",
                    cameraModel = "Motorola Edge 50 Ultra",
                    aperture = "f/1.6",
                    iso = "ISO 80",
                    shutterSpeed = "1/400s",
                    focalLength = "50mm Crop",
                    tags = "portrait, green, natural, sunlight",
                    faceNames = "Sophia Chen",
                    category = "Portraits"
                ),
                PhotoItem(
                    id = "photo_6",
                    title = "Minimalist Horizon",
                    uri = "asset_landscape_minimal",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 72,
                    dateFormatted = "Sep 22, 2026",
                    location = "Lake Michigan Coast",
                    width = 4096,
                    height = 2048,
                    fileSizeFormatted = "6.5 MB",
                    isFavorite = true,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola Edge 50 Ultra (Ultra-wide)",
                    aperture = "f/2.0",
                    iso = "ISO 64",
                    shutterSpeed = "1/2000s",
                    focalLength = "13mm 122° FOV",
                    tags = "landscape, horizon, black and white, water",
                    faceNames = "",
                    category = "Camera"
                ),
                PhotoItem(
                    id = "photo_7",
                    title = "Family Sunday - Liam & Emma",
                    uri = "asset_kids_play",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 96,
                    dateFormatted = "Sep 21, 2026",
                    location = "Millennium Park",
                    width = 3840,
                    height = 2880,
                    fileSizeFormatted = "8.9 MB",
                    isFavorite = false,
                    isVaulted = false,
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola Razr 50 Ultra",
                    aperture = "f/1.7",
                    iso = "ISO 200",
                    shutterSpeed = "1/800s Action Freeze",
                    focalLength = "24mm",
                    tags = "family, kids, park, candid",
                    faceNames = "Liam & Emma",
                    category = "Portraits"
                ),
                PhotoItem(
                    id = "photo_8",
                    title = "Confidential Financial Documents",
                    uri = "asset_vault_doc",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 120,
                    dateFormatted = "Sep 20, 2026",
                    location = "Private Office",
                    width = 3000,
                    height = 4000,
                    fileSizeFormatted = "5.2 MB",
                    isFavorite = false,
                    isVaulted = true, // Private Vault Item!
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola ThinkPhone 25 (Document Scan)",
                    aperture = "f/1.8",
                    iso = "ISO 100",
                    shutterSpeed = "1/120s",
                    focalLength = "26mm Macro",
                    tags = "confidential, vault, secure, documents",
                    faceNames = "",
                    category = "Vault"
                ),
                PhotoItem(
                    id = "photo_9",
                    title = "Secret Vault Photo 2",
                    uri = "asset_vault_id",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 140,
                    dateFormatted = "Sep 19, 2026",
                    location = "Secure Location",
                    width = 3000,
                    height = 3000,
                    fileSizeFormatted = "4.8 MB",
                    isFavorite = true,
                    isVaulted = true, // Private Vault Item!
                    isCloudSynced = true,
                    cloudSyncStatus = "SYNCED",
                    cameraModel = "Motorola ThinkPhone 25",
                    aperture = "f/1.8",
                    iso = "ISO 160",
                    shutterSpeed = "1/200s",
                    focalLength = "26mm",
                    tags = "private, vault, identity",
                    faceNames = "",
                    category = "Vault"
                ),
                PhotoItem(
                    id = "photo_10",
                    title = "Urban Shadow Geometry",
                    uri = "asset_street_shadow",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 160,
                    dateFormatted = "Sep 18, 2026",
                    location = "Downtown Subway",
                    width = 4096,
                    height = 2730,
                    fileSizeFormatted = "8.1 MB",
                    isFavorite = true,
                    isVaulted = false,
                    isCloudSynced = false,
                    cloudSyncStatus = "PENDING",
                    cameraModel = "Motorola Edge 50 Ultra",
                    aperture = "f/1.6",
                    iso = "ISO 400",
                    shutterSpeed = "1/320s",
                    focalLength = "24mm",
                    tags = "monochrome, shadow, street, subway",
                    faceNames = "",
                    category = "Street"
                )
            )

            val initialFaceClusters = listOf(
                FaceCluster("face_1", "Elena Rostova", 8, 0.98f, "photo_2", 0xFFE2E8F0),
                FaceCluster("face_2", "Sophia Chen", 6, 0.95f, "photo_5", 0xFFD4D4D8),
                FaceCluster("face_3", "Marcus Vance", 5, 0.96f, "photo_4", 0xFFA1A1AA),
                FaceCluster("face_4", "Liam & Emma", 7, 0.93f, "photo_7", 0xFF71717A)
            )

            val initialAlbums = listOf(
                AlbumItem("album_1", "Camera", 10, "photo_1", true),
                AlbumItem("album_2", "Portraits", 4, "photo_2", true),
                AlbumItem("album_3", "Motorola Night Vision", 3, "photo_3", true),
                AlbumItem("album_4", "Black & White Shots", 6, "photo_6", false),
                AlbumItem("album_5", "Ready For Transfers", 4, "photo_4", false)
            )

            photoDao.insertPhotos(initialPhotos)
            photoDao.insertFaceClusters(initialFaceClusters)
            photoDao.insertAlbums(initialAlbums)
        }
    }

    suspend fun setFavorite(photoId: String, isFav: Boolean) {
        photoDao.setPhotoFavorite(photoId, isFav)
    }

    suspend fun moveToVault(photoId: String) {
        photoDao.setPhotoVaulted(photoId, true)
    }

    suspend fun restoreFromVault(photoId: String) {
        photoDao.setPhotoVaulted(photoId, false)
    }

    suspend fun deletePhoto(photoId: String) {
        photoDao.deletePhoto(photoId)
    }

    fun searchPhotos(query: String): Flow<List<PhotoItem>> {
        return photoDao.searchPhotos(query)
    }

    fun getPhotosForPerson(personName: String): Flow<List<PhotoItem>> {
        return photoDao.getPhotosForPerson(personName)
    }

    suspend fun renamePerson(clusterId: String, newName: String) {
        photoDao.renameFaceCluster(clusterId, newName)
    }

    fun unlockVault(enteredPin: String): Boolean {
        if (enteredPin == _vaultPin.value) {
            _isVaultUnlocked.value = true
            _failedUnlockAttempts.value = 0
            return true
        } else {
            _failedUnlockAttempts.value += 1
            if (_failedUnlockAttempts.value >= 3) {
                PushNotificationHelper.showVaultAlertNotification(
                    context,
                    "Security Alert: ${_failedUnlockAttempts.value} incorrect PIN attempts detected on Private Vault."
                )
            }
            return false
        }
    }

    fun unlockVaultWithBiometrics(): Boolean {
        _isVaultUnlocked.value = true
        _failedUnlockAttempts.value = 0
        return true
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
    }

    fun setBackupRule(rule: BackupRule) {
        _backupRule.value = rule
    }

    suspend fun runFaceClusteringScan(): Int = withContext(Dispatchers.IO) {
        // Runs on-device offline clustering simulation
        kotlinx.coroutines.delay(1200) // Authentic processing delay
        val newCount = 4
        PushNotificationHelper.showFaceClusteringNotification(context, "Elena Rostova", 8)
        newCount
    }

    suspend fun triggerCloudBackup(): Int = withContext(Dispatchers.IO) {
        _isBackingUp.value = true
        _backupProgress.value = 0.2f
        kotlinx.coroutines.delay(600)
        _backupProgress.value = 0.6f
        kotlinx.coroutines.delay(600)
        _backupProgress.value = 1.0f

        // Mark pending items synced
        photoDao.markCloudSynced("photo_5")
        photoDao.markCloudSynced("photo_10")
        _isBackingUp.value = false

        PushNotificationHelper.showBackupCompletedNotification(
            context,
            itemsSynced = 2,
            batterySavedPct = 18
        )
        2
    }
}
