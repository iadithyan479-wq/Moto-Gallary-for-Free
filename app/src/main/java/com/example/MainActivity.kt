package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GalleryViewModel
import com.example.ui.components.GesturePhotoViewer
import com.example.ui.components.OnboardingTutorial
import com.example.ui.components.PushNotificationHelper
import com.example.ui.components.VoiceCommandBar
import com.example.ui.components.tr
import com.example.ui.screens.AlbumsScreen
import com.example.ui.screens.DiscoveryScreen
import com.example.ui.screens.PersonDetailScreen
import com.example.ui.screens.PhotosScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MotoGalleryTheme
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PushNotificationHelper.initNotificationChannels(this)

        setContent {
            MotoGalleryTheme {
                MotoGalleryApp()
            }
        }
    }
}

@Composable
fun MotoGalleryApp(viewModel: GalleryViewModel = viewModel()) {
    val context = LocalContext.current

    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val activePhotoIndex by viewModel.activePhotoIndex.collectAsState()
    val viewingVaultPhotos by viewModel.viewingVaultPhotos.collectAsState()
    val selectedPersonCluster by viewModel.selectedPersonCluster.collectAsState()
    val isVoiceBarVisible by viewModel.isVoiceBarVisible.collectAsState()
    val isVaultInSettingsOpen by viewModel.isVaultInSettingsOpen.collectAsState()

    val publicPhotos by viewModel.publicPhotos.collectAsState()
    val vaultPhotos by viewModel.vaultPhotos.collectAsState()
    val faceClusters by viewModel.faceClusters.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val failedUnlockAttempts by viewModel.failedUnlockAttempts.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val backupRule by viewModel.backupRule.collectAsState()
    val isBackingUp by viewModel.isBackingUp.collectAsState()
    val backupProgress by viewModel.backupProgress.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val gridColumns by viewModel.gridColumns.collectAsState()

    // Permissions launcher for Android 13+ Push Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    if (!isOnboardingCompleted) {
        OnboardingTutorial(onFinished = { viewModel.completeOnboarding() })
        return
    }

    // Hardware Back Handling
    BackHandler(enabled = activePhotoIndex != null || selectedPersonCluster != null || isVaultInSettingsOpen || currentTab != "photos") {
        when {
            activePhotoIndex != null -> viewModel.closePhotoViewer()
            selectedPersonCluster != null -> viewModel.selectPersonCluster(null)
            isVaultInSettingsOpen -> viewModel.closeVaultInSettings()
            currentTab != "photos" -> viewModel.setTab("photos")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PitchBlack)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PitchBlack,
            contentColor = PureWhite,
            bottomBar = {
                NavigationBar(
                    containerColor = PitchBlack,
                    contentColor = PureWhite,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF1E1E24))
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav")
                ) {
                    val tabs = listOf(
                        Triple("photos", tr("photos"), Icons.Default.PhotoLibrary),
                        Triple("albums", tr("albums"), Icons.Default.Folder),
                        Triple("for_you", tr("for_you"), Icons.Default.AutoAwesome),
                        Triple("settings", tr("settings"), Icons.Default.Settings)
                    )

                    tabs.forEach { (tabId, label, icon) ->
                        val isSelected = currentTab == tabId && selectedPersonCluster == null
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(tabId) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PitchBlack,
                                selectedTextColor = PureWhite,
                                indicatorColor = PureWhite,
                                unselectedIconColor = Color(0xFFA1A1AA),
                                unselectedTextColor = Color(0xFFA1A1AA)
                            ),
                            modifier = Modifier.testTag("nav_tab_$tabId")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // If a specific person is clicked in Albums, show person details
                if (selectedPersonCluster != null) {
                    val cluster = selectedPersonCluster!!
                    val personPhotos = publicPhotos.filter { it.faceNames.contains(cluster.personName, ignoreCase = true) }
                    PersonDetailScreen(
                        cluster = cluster,
                        photos = personPhotos,
                        onBack = { viewModel.selectPersonCluster(null) },
                        onPhotoClick = { photo ->
                            val idx = publicPhotos.indexOfFirst { it.id == photo.id }
                            if (idx >= 0) viewModel.openPhotoViewer(idx)
                        },
                        onRenamePerson = { newName ->
                            viewModel.renamePerson(cluster.id, newName)
                        }
                    )
                } else {
                    when (currentTab) {
                        "photos" -> {
                            PhotosScreen(
                                photos = publicPhotos,
                                onPhotoClick = { idx -> viewModel.openPhotoViewer(idx, isFromVault = false) },
                                onVoiceClick = { viewModel.setVoiceBarVisible(true) },
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                selectedCategory = selectedCategory,
                                onCategoryChange = { viewModel.setSelectedCategory(it) },
                                gridColumns = gridColumns,
                                onGridColumnsChange = { viewModel.setGridColumns(it) }
                            )
                        }
                        "albums" -> {
                            AlbumsScreen(
                                albums = albums,
                                faceClusters = faceClusters,
                                onAlbumClick = { album ->
                                    viewModel.setTab("photos")
                                    viewModel.setSelectedCategory(album.title)
                                },
                                onPersonClick = { cluster ->
                                    viewModel.selectPersonCluster(cluster)
                                },
                                onScanFaces = {
                                    viewModel.scanOfflineFaces()
                                }
                            )
                        }
                        "for_you" -> {
                            DiscoveryScreen(
                                photos = publicPhotos,
                                onPhotoClick = { photo ->
                                    val idx = publicPhotos.indexOfFirst { it.id == photo.id }
                                    if (idx >= 0) viewModel.openPhotoViewer(idx)
                                }
                            )
                        }
                        "settings" -> {
                            val totalMediaMB = remember(publicPhotos) { publicPhotos.size * 8.6f }
                            val vaultedMB = remember(vaultPhotos) { vaultPhotos.size * 6.2f }
                            SettingsScreen(
                                isVaultOpen = isVaultInSettingsOpen,
                                onOpenVault = { viewModel.openVaultInSettings() },
                                onCloseVault = { viewModel.closeVaultInSettings() },
                                isVaultUnlocked = isVaultUnlocked,
                                vaultPhotos = vaultPhotos,
                                onUnlockWithPin = { pin -> viewModel.unlockVaultWithPin(pin) },
                                onUnlockWithBiometrics = { viewModel.unlockVaultWithBiometrics() },
                                onLockVault = { viewModel.lockVault() },
                                onVaultPhotoClick = { photo ->
                                    val idx = vaultPhotos.indexOfFirst { it.id == photo.id }
                                    if (idx >= 0) viewModel.openPhotoViewer(idx, isFromVault = true)
                                },
                                failedAttempts = failedUnlockAttempts,
                                backupRule = backupRule,
                                onBackupRuleChange = { viewModel.setBackupRule(it) },
                                isBackingUp = isBackingUp,
                                backupProgress = backupProgress,
                                onTriggerBackup = { viewModel.triggerCloudBackup() },
                                pairedDevices = pairedDevices,
                                totalMediaMB = totalMediaMB,
                                vaultedMB = vaultedMB
                            )
                        }
                    }
                }

                // Voice Command overlay
                VoiceCommandBar(
                    isVisible = isVoiceBarVisible,
                    onDismiss = { viewModel.setVoiceBarVisible(false) },
                    onCommandRecognized = { cmd ->
                        viewModel.executeVoiceCommand(cmd)
                        viewModel.setVoiceBarVisible(false)
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Fullscreen Gesture Photo Previewer overlay
        val displayedPhotos = if (viewingVaultPhotos) vaultPhotos else publicPhotos
        if (activePhotoIndex != null && displayedPhotos.isNotEmpty()) {
            GesturePhotoViewer(
                photos = displayedPhotos,
                initialIndex = activePhotoIndex!!.coerceIn(0, displayedPhotos.size - 1),
                onDismiss = { viewModel.closePhotoViewer() },
                onToggleFavorite = { photo -> viewModel.toggleFavorite(photo) },
                onMoveToVault = { photo ->
                    if (viewingVaultPhotos) {
                        viewModel.restoreFromVault(photo)
                    } else {
                        viewModel.moveToVault(photo)
                    }
                },
                onDeletePhoto = { photo -> viewModel.deletePhoto(photo) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
