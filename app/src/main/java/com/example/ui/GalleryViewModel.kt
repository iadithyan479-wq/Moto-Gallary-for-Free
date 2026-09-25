package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AlbumItem
import com.example.data.model.BackupRule
import com.example.data.model.FaceCluster
import com.example.data.model.PairedDevice
import com.example.data.model.PhotoItem
import com.example.data.repository.GalleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("motogallery_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getInstance(application)
    private val repository = GalleryRepository(application, database.photoDao())

    val publicPhotos: StateFlow<List<PhotoItem>> = repository.publicPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultPhotos: StateFlow<List<PhotoItem>> = repository.vaultPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePhotos: StateFlow<List<PhotoItem>> = repository.favoritePhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val faceClusters: StateFlow<List<FaceCluster>> = repository.faceClusters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<AlbumItem>> = repository.albums
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isVaultUnlocked: StateFlow<Boolean> = repository.isVaultUnlocked
    val failedUnlockAttempts: StateFlow<Int> = repository.failedUnlockAttempts
    val backupRule: StateFlow<BackupRule> = repository.backupRule
    val isBackingUp: StateFlow<Boolean> = repository.isBackingUp
    val backupProgress: StateFlow<Float> = repository.backupProgress
    val pairedDevices: StateFlow<List<PairedDevice>> = repository.pairedDevices

    private val _currentTab = MutableStateFlow("photos") // photos, albums, for_you, settings
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _isVaultInSettingsOpen = MutableStateFlow(false)
    val isVaultInSettingsOpen: StateFlow<Boolean> = _isVaultInSettingsOpen.asStateFlow()

    private val _activePhotoIndex = MutableStateFlow<Int?>(null)
    val activePhotoIndex: StateFlow<Int?> = _activePhotoIndex.asStateFlow()

    private val _viewingVaultPhotos = MutableStateFlow(false)
    val viewingVaultPhotos: StateFlow<Boolean> = _viewingVaultPhotos.asStateFlow()

    private val _selectedPersonCluster = MutableStateFlow<FaceCluster?>(null)
    val selectedPersonCluster: StateFlow<FaceCluster?> = _selectedPersonCluster.asStateFlow()

    private val _isVoiceBarVisible = MutableStateFlow(false)
    val isVoiceBarVisible: StateFlow<Boolean> = _isVoiceBarVisible.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _gridColumns = MutableStateFlow(3)
    val gridColumns: StateFlow<Int> = _gridColumns.asStateFlow()

    fun setTab(tab: String) {
        _currentTab.value = tab
        _selectedPersonCluster.value = null
        if (tab != "settings") {
            _isVaultInSettingsOpen.value = false
        }
    }

    fun openVaultInSettings() {
        _isVaultInSettingsOpen.value = true
    }

    fun closeVaultInSettings() {
        _isVaultInSettingsOpen.value = false
    }

    fun openPhotoViewer(index: Int, isFromVault: Boolean = false) {
        _viewingVaultPhotos.value = isFromVault
        _activePhotoIndex.value = index
    }

    fun closePhotoViewer() {
        _activePhotoIndex.value = null
    }

    fun selectPersonCluster(cluster: FaceCluster?) {
        _selectedPersonCluster.value = cluster
    }

    fun setVoiceBarVisible(visible: Boolean) {
        _isVoiceBarVisible.value = visible
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setGridColumns(cols: Int) {
        _gridColumns.value = cols.coerceIn(2, 4)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        _isOnboardingCompleted.value = true
    }

    fun toggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            repository.setFavorite(photo.id, !photo.isFavorite)
        }
    }

    fun moveToVault(photo: PhotoItem) {
        viewModelScope.launch {
            repository.moveToVault(photo.id)
        }
    }

    fun restoreFromVault(photo: PhotoItem) {
        viewModelScope.launch {
            repository.restoreFromVault(photo.id)
        }
    }

    fun deletePhoto(photo: PhotoItem) {
        viewModelScope.launch {
            repository.deletePhoto(photo.id)
        }
    }

    fun unlockVaultWithPin(pin: String): Boolean {
        return repository.unlockVault(pin)
    }

    fun unlockVaultWithBiometrics(): Boolean {
        return repository.unlockVaultWithBiometrics()
    }

    fun lockVault() {
        repository.lockVault()
    }

    fun setBackupRule(rule: BackupRule) {
        repository.setBackupRule(rule)
    }

    suspend fun triggerCloudBackup(): Int {
        return repository.triggerCloudBackup()
    }

    suspend fun scanOfflineFaces(): Int {
        return repository.runFaceClusteringScan()
    }

    fun renamePerson(clusterId: String, newName: String) {
        viewModelScope.launch {
            repository.renamePerson(clusterId, newName)
            _selectedPersonCluster.value = _selectedPersonCluster.value?.copy(personName = newName)
        }
    }

    fun executeVoiceCommand(command: String) {
        val lower = command.lowercase().trim()
        when {
            lower.contains("vault") || lower.contains("lock") -> {
                _currentTab.value = "settings"
                _isVaultInSettingsOpen.value = true
            }
            lower.contains("setting") || lower.contains("preference") -> {
                _currentTab.value = "settings"
                _isVaultInSettingsOpen.value = false
            }
            lower.contains("photos") || lower.contains("gallery") -> {
                _currentTab.value = "photos"
                _selectedCategory.value = "All"
            }
            lower.contains("albums") || lower.contains("collections") -> {
                _currentTab.value = "albums"
            }
            lower.contains("for you") || lower.contains("discovery") || lower.contains("memories") -> {
                _currentTab.value = "for_you"
            }
            lower.contains("favorite") -> {
                _currentTab.value = "photos"
                _selectedCategory.value = "Favorites"
            }
            lower.contains("night") -> {
                _currentTab.value = "photos"
                _selectedCategory.value = "Night Vision"
            }
            lower.contains("portrait") -> {
                _currentTab.value = "photos"
                _selectedCategory.value = "Portraits"
            }
            lower.contains("sync") || lower.contains("backup") -> {
                _currentTab.value = "settings"
                viewModelScope.launch {
                    triggerCloudBackup()
                }
            }
            lower.contains("scan") || lower.contains("face") -> {
                _currentTab.value = "albums"
                viewModelScope.launch {
                    scanOfflineFaces()
                }
            }
            lower.contains("grid 2") || lower.contains("two columns") || lower.contains("2 columns") -> {
                _gridColumns.value = 2
            }
            lower.contains("grid 3") || lower.contains("three columns") || lower.contains("3 columns") -> {
                _gridColumns.value = 3
            }
            lower.contains("grid 4") || lower.contains("four columns") || lower.contains("4 columns") -> {
                _gridColumns.value = 4
            }
        }
    }
}
