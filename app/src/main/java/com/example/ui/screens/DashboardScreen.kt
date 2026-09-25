package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackupRule
import com.example.data.model.PairedDevice
import com.example.data.model.PhotoItem
import com.example.ui.components.PushNotificationHelper
import com.example.ui.components.tr
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecurityGreen
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    photos: List<PhotoItem>,
    vaultPhotosCount: Int,
    pairedDevices: List<PairedDevice>,
    backupRule: BackupRule,
    onBackupRuleChange: (BackupRule) -> Unit,
    isBackingUp: Boolean,
    backupProgress: Float,
    onTriggerBackup: suspend () -> Int,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var lastBackupMessage by remember { mutableStateOf<String?>(null) }

    val totalMediaMB = remember(photos) {
        photos.size * 8.6f
    }
    val vaultedMB = remember(vaultPhotosCount) {
        vaultPhotosCount * 6.2f
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 36.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Dashboard Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANALYTICS & SYNC",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = PureWhite
                    )
                }
                Text(
                    text = "DATA VISUALIZATION • E2E BACKUP & BATTERY SAVER",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        // Section 1: Data Visualization - Monthly Capture Trends (Canvas Bar Chart)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = tr("engagement_trends"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Photos captured on Motorola hardware (Last 6 Months)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Canvas Bar Chart
                val months = listOf("Apr", "May", "Jun", "Jul", "Aug", "Sep")
                val counts = listOf(42, 65, 88, 120, 95, 140)
                val maxVal = 160f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val barWidth = (w / months.size) * 0.5f
                    val step = w / months.size

                    // Baseline
                    drawLine(
                        color = Color(0xFF27272A),
                        start = Offset(0f, h - 20f),
                        end = Offset(w, h - 20f),
                        strokeWidth = 2f
                    )

                    counts.forEachIndexed { i, count ->
                        val barHeight = ((count / maxVal) * (h - 40f))
                        val left = (i * step) + (step - barWidth) / 2
                        val top = (h - 20f) - barHeight

                        drawRoundRect(
                            color = if (i == counts.size - 1) PureWhite else Color(0xFF71717A),
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }

                // Month labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    months.forEach { m ->
                        Text(
                            text = m,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }

        // Section 2: Storage Telemetry (Photos vs Vault vs Synced)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F12), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = tr("storage_breakdown"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Total local allocation: ${(totalMediaMB + vaultedMB).toInt()} MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar Breakdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Box(modifier = Modifier.weight(0.65f).fillMaxSize().background(PureWhite))
                    Box(modifier = Modifier.weight(0.2f).fillMaxSize().background(Color(0xFF71717A)))
                    Box(modifier = Modifier.weight(0.15f).fillMaxSize().background(SecurityGreen))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StorageLegendItem("Gallery Media", "${totalMediaMB.toInt()} MB", PureWhite)
                    StorageLegendItem("Private Vault", "${vaultedMB.toInt()} MB", Color(0xFF71717A))
                    StorageLegendItem("E2E Backed Up", "100%", SecurityGreen)
                }
            }
        }

        // Section 3: Battery-Optimized Cloud Sync Manager
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
                    text = "End-to-end encrypted backup intelligently throttled when unplugged",
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

        // Section 4: Cross-Platform Paired Devices (Motorola Ready For)
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

        // Section 5: Real-Time System Notification Alert Simulator
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
                        text = "Real-Time Push Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
                Text(
                    text = "Trigger real device push notifications for system and security events",
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
                        Text(text = "Backup Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            PushNotificationHelper.showVaultAlertNotification(context, "Suspicious PIN access blocked")
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
    }
}

@Composable
private fun StorageLegendItem(label: String, value: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFA1A1AA))
            Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PureWhite)
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
