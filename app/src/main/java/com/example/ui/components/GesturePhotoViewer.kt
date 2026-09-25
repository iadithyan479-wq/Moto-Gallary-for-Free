package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoItem
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VaultRed
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturePhotoViewer(
    photos: List<PhotoItem>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onToggleFavorite: (PhotoItem) -> Unit,
    onMoveToVault: (PhotoItem) -> Unit,
    onDeletePhoto: (PhotoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onDismiss() }

    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    val currentPhoto = photos.getOrNull(currentIndex) ?: return

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showChrome by remember { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf(PhotoFilter.ORIGINAL) }

    val context = LocalContext.current

    fun resetZoom() {
        scale = 1f
        offset = Offset.Zero
    }

    fun sharePhoto(photo: PhotoItem) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Shared via Motorola MotoGallery: ${photo.title}"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "${photo.title} (${photo.cameraModel})\nResolution: ${photo.width}x${photo.height}\nTaken at: ${photo.location}\nShared via Motorola Ready For / MotoGallery"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Photo via"))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("gesture_photo_viewer")
    ) {
        // Zoomable and Pannable Image Canvas
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentIndex) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = max(1f, min(scale * zoom, 4.5f))
                        if (scale > 1f) {
                            val maxX = (size.width * (scale - 1)) / 2
                            val maxY = (size.height * (scale - 1)) / 2
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        } else {
                            offset = Offset.Zero
                        }
                    }
                }
                .pointerInput(currentIndex) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                resetZoom()
                            } else {
                                scale = 2.5f
                            }
                        },
                        onTap = {
                            showChrome = !showChrome
                        }
                    )
                }
        ) {
            PhotoCanvasImage(
                photoId = currentPhoto.id,
                filter = currentFilter,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }

        // Navigation Chevron overlays (swipe or tap)
        if (scale == 1f) {
            if (currentIndex > 0) {
                IconButton(
                    onClick = {
                        resetZoom()
                        currentIndex--
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("viewer_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Photo",
                        tint = PureWhite
                    )
                }
            }

            if (currentIndex < photos.size - 1) {
                IconButton(
                    onClick = {
                        resetZoom()
                        currentIndex++
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("viewer_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Photo",
                        tint = PureWhite
                    )
                }
            }
        }

        // Top App Bar Controls
        AnimatedVisibility(
            visible = showChrome,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp).testTag("viewer_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Viewer",
                        tint = PureWhite
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentPhoto.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "${currentIndex + 1} of ${photos.size} • ${currentPhoto.dateFormatted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )
                }

                IconButton(
                    onClick = { showInfoSheet = true },
                    modifier = Modifier.size(48.dp).testTag("viewer_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View EXIF Info",
                        tint = PureWhite
                    )
                }
            }
        }

        // Bottom Bar Action Controls
        AnimatedVisibility(
            visible = showChrome,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                // Photo Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhotoFilter.values().forEach { filter ->
                        val isSelected = currentFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) PureWhite else Color(0xFF18181B))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(16.dp))
                                .clickable { currentFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("filter_chip_${filter.name}")
                        ) {
                            Text(
                                text = filter.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) PitchBlack else PureWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Favorite, Share, Move to Vault, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorite
                    IconButton(
                        onClick = { onToggleFavorite(currentPhoto) },
                        modifier = Modifier.size(48.dp).testTag("viewer_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (currentPhoto.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Photo",
                            tint = if (currentPhoto.isFavorite) PureWhite else Color(0xFFA1A1AA)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = { sharePhoto(currentPhoto) },
                        modifier = Modifier.size(48.dp).testTag("viewer_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Photo",
                            tint = PureWhite
                        )
                    }

                    // Move to Vault
                    IconButton(
                        onClick = {
                            onMoveToVault(currentPhoto)
                            onDismiss()
                        },
                        modifier = Modifier.size(48.dp).testTag("viewer_vault_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Move to Private Vault",
                            tint = PureWhite
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = {
                            onDeletePhoto(currentPhoto)
                            if (photos.size <= 1) {
                                onDismiss()
                            } else {
                                currentIndex = currentIndex.coerceAtMost(photos.size - 2)
                            }
                        },
                        modifier = Modifier.size(48.dp).testTag("viewer_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Photo",
                            tint = VaultRed
                        )
                    }
                }
            }
        }

        // EXIF & Telemetry Bottom Sheet
        if (showInfoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = Color(0xFF0F0F12),
                contentColor = PureWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Camera & Capture Telemetry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "High-resolution EXIF metadata",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExifRow("Camera Model", currentPhoto.cameraModel)
                    ExifRow("Focal Length", currentPhoto.focalLength)
                    ExifRow("Aperture", currentPhoto.aperture)
                    ExifRow("Shutter Speed", currentPhoto.shutterSpeed)
                    ExifRow("ISO Sensitivity", currentPhoto.iso)
                    ExifRow("Resolution", "${currentPhoto.width} × ${currentPhoto.height}")
                    ExifRow("File Size", currentPhoto.fileSizeFormatted)
                    ExifRow("Captured At", currentPhoto.location)
                    ExifRow("Timestamp", currentPhoto.dateFormatted)

                    if (currentPhoto.faceNames.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Detected Faces (Offline Recognition)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentPhoto.faceNames,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tags & AI Labels",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentPhoto.tags,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ExifRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFA1A1AA))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = PureWhite)
    }
}
