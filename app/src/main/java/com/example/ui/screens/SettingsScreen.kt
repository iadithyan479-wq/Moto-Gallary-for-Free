package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackupRule
import com.example.data.model.PairedDevice
import com.example.data.model.PhotoItem
import com.example.ui.components.AppLanguage
import com.example.ui.components.LanguageManager
import com.example.ui.components.PushNotificationHelper
import com.example.ui.components.tr
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.VaultRed
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    isVaultOpen: Boolean,
    onOpenVault: () -> Unit,
    onCloseVault: () -> Unit,
    isVaultUnlocked: Boolean,
    vaultPhotos: List<PhotoItem>,
    onUnlockWithPin: (String) -> Boolean,
    onUnlockWithBiometrics: () -> Boolean,
    onLockVault: () -> Unit,
    onVaultPhotoClick: (PhotoItem) -> Unit,
    failedAttempts: Int,
    backupRule: BackupRule,
    onBackupRuleChange: (BackupRule) -> Unit,
    isBackingUp: Boolean,
    backupProgress: Float,
    onTriggerBackup: suspend () -> Int,
    pairedDevices: List<PairedDevice>,
    totalMediaMB: Float,
    vaultedMB: Float,
    modifier: Modifier = Modifier
) {
    if (isVaultOpen) {
        VaultScreen(
            isUnlocked = isVaultUnlocked,
            vaultPhotos = vaultPhotos,
            onUnlockWithPin = onUnlockWithPin,
            onUnlockWithBiometrics = onUnlockWithBiometrics,
            onLockVault = onLockVault,
            onPhotoClick = onVaultPhotoClick,
            failedAttempts = failedAttempts,
            onBack = onCloseVault,
            modifier = modifier
        )
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var lastBackupMessage by remember { mutableStateOf<String?>(null) }
    var showLanguageMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 36.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SETTINGS & PRIVACY",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = PureWhite
                    )
                }
                Text(
                    text = "HARDWARE VAULT • BATTERY CLOUD SYNC • NO ADS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        // Section 1: PRIVATE VAULT (Moved here per user request)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.5.dp, PureWhite, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenVault() }
                    .padding(18.dp)
                    .testTag("settings_private_vault_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF18181B))
                                .border(1.dp, PureWhite, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Private Vault",
                                tint = if (isVaultUnlocked) SecurityGreen else PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tr("vault_title"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = PureWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isVaultUnlocked) SecurityGreen.copy(alpha = 0.2f)
                                            else Color(0xFF27272A)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isVaultUnlocked) "UNLOCKED" else "LOCKED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isVaultUnlocked) SecurityGreen else Color(0xFFA1A1AA)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isVaultUnlocked)
                                    "${vaultPhotos.size} items protected • Tap to manage"
                                else
                                    "AES-256 E2E Hardware Encrypted • Tap to open",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Private Vault",
                        tint = PureWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (isVaultUnlocked) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onLockVault,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF27272A),
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("settings_lock_vault_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Lock Vault Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 2: Battery-Aware Cloud Backup & Storage
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = SecurityGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Battery-Aware Cloud Backup",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(SecurityGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Saved ~18% Battery",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecurityGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "End-to-end encrypted backup automatically throttled when unplugged",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Radio options for Battery Preservation
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BackupRadioOption(
                        title = "Wi-Fi & Charging Only (Recommended)",
                        description = "Zero battery impact • Automated overnight sync",
                        isSelected = backupRule == BackupRule.WIFI_AND_CHARGING,
                        onClick = { onBackupRuleChange(BackupRule.WIFI_AND_CHARGING) }
                    )
                    BackupRadioOption(
                        title = "Wi-Fi Only",
                        description = "Syncs on unmetered networks regardless of battery state",
                        isSelected = backupRule == BackupRule.WIFI_ONLY,
                        onClick = { onBackupRuleChange(BackupRule.WIFI_ONLY) }
                    )
                    BackupRadioOption(
                        title = "Continuous Sync (Cellular + Wi-Fi)",
                        description = "Immediate sync • Uses background power",
                        isSelected = backupRule == BackupRule.ALWAYS,
                        onClick = { onBackupRuleChange(BackupRule.ALWAYS) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isBackingUp) {
                    Column {
                        LinearProgressIndicator(
                            progress = { backupProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = PureWhite,
                            trackColor = Color(0xFF27272A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Encrypting and uploading via AES-256...",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val count = onTriggerBackup()
                                lastBackupMessage = "Successfully backed up $count pending items!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("trigger_backup_button")
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = tr("cloud_backup_now"), fontWeight = FontWeight.Bold)
                    }
                }

                if (lastBackupMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lastBackupMessage!!,
                        color = SecurityGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Section 3: Cross-Platform Paired Devices (Motorola Ready For)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tr("ready_for"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
                Text(
                    text = "End-to-end encrypted synchronization across Motorola ecosystem",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(12.dp))

                pairedDevices.forEach { device ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (device.type) {
                                    "pc_ready_for" -> Icons.Default.Computer
                                    "tablet" -> Icons.Default.Tablet
                                    else -> Icons.Default.PhoneAndroid
                                },
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = device.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Last synced: ${device.lastSyncFormatted}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (device.isConnected) SecurityGreen else Color(0xFF71717A))
                        )
                    }
                }
            }
        }

        // Section 4: Language & Regional Preferences
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Language & Localization",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Box {
                        Button(
                            onClick = { showLanguageMenu = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E1E24),
                                contentColor = PureWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("settings_lang_button")
                        ) {
                            Text(
                                text = LanguageManager.currentLanguage.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false },
                            modifier = Modifier.background(PitchBlack).border(1.dp, Color(0xFF3F3F46))
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${lang.displayName} (${lang.nativeName})",
                                            color = if (LanguageManager.currentLanguage == lang) PureWhite else Color(0xFFA1A1AA),
                                            fontWeight = if (LanguageManager.currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        LanguageManager.currentLanguage = lang
                                        showLanguageMenu = false
                                    },
                                    modifier = Modifier.testTag("settings_lang_item_${lang.code}")
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Current: ${LanguageManager.currentLanguage.nativeName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        // Section 5: Real-Time Alerts & Notification Simulator
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "System Alerts & Security Push",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
                Text(
                    text = "Instant notification channels for vault tampering and backup finishes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            PushNotificationHelper.showBackupCompletedNotification(context, 12, 18)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E1E24),
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("test_backup_notif_button")
                    ) {
                        Text(text = "Backup Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            PushNotificationHelper.showVaultAlertNotification(context, "Unauthorized Vault access blocked")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E1E24),
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("test_vault_notif_button")
                    ) {
                        Text(text = "Vault Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 6: Display & Accessibility
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DisplaySettings,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Display & Accessibility",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Pitch Black High-Contrast OLED", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PureWhite)
                        Text(text = "Zero pixel illumination on Motorola AMOLED screens", style = MaterialTheme.typography.bodySmall, color = Color(0xFFA1A1AA))
                    }
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SecurityGreen, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Accessible 48dp Touch Targets", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PureWhite)
                        Text(text = "Guaranteed touch target precision for accessibility", style = MaterialTheme.typography.bodySmall, color = Color(0xFFA1A1AA))
                    }
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SecurityGreen, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Section 7: About
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About MotoGallery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MotoGallery v1.0.0 (Release Build)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "100% Free Forever • Zero Advertisements • Zero Cloud Telemetry • Tailored for Motorola Edge, Razr, and ThinkPhone devices.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )
            }
        }
    }
}

@Composable
private fun BackupRadioOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PureWhite,
                unselectedColor = Color(0xFF71717A)
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFA1A1AA)
            )
        }
    }
}
