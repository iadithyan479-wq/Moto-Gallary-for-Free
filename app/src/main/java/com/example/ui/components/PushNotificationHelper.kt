package com.example.ui.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object PushNotificationHelper {

    private const val CHANNEL_SECURITY_ID = "motogallery_security_channel"
    private const val CHANNEL_BACKUP_ID = "motogallery_backup_channel"
    private const val CHANNEL_DISCOVERY_ID = "motogallery_discovery_channel"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val securityChannel = NotificationChannel(
                CHANNEL_SECURITY_ID,
                "Security & Vault Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts regarding Vault access attempts and encryption status"
                enableVibration(true)
            }

            val backupChannel = NotificationChannel(
                CHANNEL_BACKUP_ID,
                "Cloud Backup & Battery Status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Status of battery-optimized encrypted cloud synchronizations"
            }

            val discoveryChannel = NotificationChannel(
                CHANNEL_DISCOVERY_ID,
                "Face Recognition & Memories",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Offline face clustering updates and personalized discovery feeds"
            }

            notificationManager.createNotificationChannel(securityChannel)
            notificationManager.createNotificationChannel(backupChannel)
            notificationManager.createNotificationChannel(discoveryChannel)
        }
    }

    fun showBackupCompletedNotification(context: Context, itemsSynced: Int, batterySavedPct: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BACKUP_ID)
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle("Cloud Sync Finished")
            .setContentText("Synced $itemsSynced items with end-to-end encryption. Saved ~$batterySavedPct% battery.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("All pending photos uploaded to private cloud storage with zero unmetered battery usage (Moto Smart Doze applied)."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    fun showVaultAlertNotification(context: Context, alertMessage: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SECURITY_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Vault Security Guard")
            .setContentText(alertMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$alertMessage. High-grade AES-256 GCM storage remains sealed."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1002, notification)
    }

    fun showFaceClusteringNotification(context: Context, personName: String, photoCount: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_DISCOVERY_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("New Offline Face Grouped")
            .setContentText("Indexed $photoCount photos containing '$personName' completely on-device.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1003, notification)
    }
}
