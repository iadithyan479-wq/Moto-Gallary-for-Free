package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoItem
import com.example.ui.components.AppLanguage
import com.example.ui.components.LanguageManager
import com.example.ui.components.PhotoCanvasImage
import com.example.ui.components.tr
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite

@Composable
fun PhotosScreen(
    photos: List<PhotoItem>,
    onPhotoClick: (Int) -> Unit,
    onVoiceClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    gridColumns: Int,
    onGridColumnsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showSearchField by remember { mutableStateOf(false) }

    // Pinch-to-zoom columns gesture tracker
    var totalZoom by remember { mutableFloatStateOf(1f) }

    val categories = listOf("All", "Favorites", "Portraits", "Night Vision", "Camera", "Street")

    val filteredPhotos = photos.filter { photo ->
        val matchesCategory = when (selectedCategory) {
            "Favorites" -> photo.isFavorite
            "Portraits" -> photo.category == "Portraits" || photo.faceNames.isNotEmpty()
            "Night Vision" -> photo.category == "Night Vision"
            "Camera" -> photo.category == "Camera"
            "Street" -> photo.category == "Street"
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            photo.title.contains(searchQuery, ignoreCase = true) ||
            photo.location.contains(searchQuery, ignoreCase = true) ||
            photo.tags.contains(searchQuery, ignoreCase = true) ||
            photo.faceNames.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .pointerInput(gridColumns) {
                detectTransformGestures { _, _, zoom, _ ->
                    totalZoom *= zoom
                    if (totalZoom > 1.4f && gridColumns > 2) {
                        onGridColumnsChange(gridColumns - 1)
                        totalZoom = 1f
                    } else if (totalZoom < 0.7f && gridColumns < 4) {
                        onGridColumnsChange(gridColumns + 1)
                        totalZoom = 1f
                    }
                }
            }
            .testTag("photos_screen")
    ) {
        // High-Contrast Minimal Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MOTO GALLERY",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = PureWhite
                )
                Text(
                    text = "MOTOROLA PURE MONOCHROME • 100% OFFLINE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = Color(0xFFA1A1AA)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Search toggle
                IconButton(
                    onClick = { showSearchField = !showSearchField },
                    modifier = Modifier.size(48.dp).testTag("search_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Photos",
                        tint = if (showSearchField) PureWhite else Color(0xFFA1A1AA)
                    )
                }

                // Grid switcher
                IconButton(
                    onClick = {
                        val nextCols = when (gridColumns) {
                            2 -> 3
                            3 -> 4
                            else -> 2
                        }
                        onGridColumnsChange(nextCols)
                    },
                    modifier = Modifier.size(48.dp).testTag("grid_toggle_button")
                ) {
                    Icon(
                        imageVector = when (gridColumns) {
                            2 -> Icons.Default.GridOn
                            3 -> Icons.Default.Grid3x3
                            else -> Icons.Default.Grid4x4
                        },
                        contentDescription = "Switch Grid ($gridColumns columns)",
                        tint = PureWhite
                    )
                }

                // Voice Command Mic
                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier.size(48.dp).testTag("voice_command_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Commands",
                        tint = PureWhite
                    )
                }

                // Language Switcher
                Box {
                    IconButton(
                        onClick = { showLanguageMenu = true },
                        modifier = Modifier.size(48.dp).testTag("language_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = PureWhite
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
                                modifier = Modifier.testTag("lang_item_${lang.code}")
                            )
                        }
                    }
                }
            }
        }

        // Search Input field if toggled
        AnimatedVisibility(
            visible = showSearchField,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = tr("search_hint"),
                        color = Color(0xFF71717A),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = Color(0xFF3F3F46),
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    focusedContainerColor = Color(0xFF0F0F12),
                    unfocusedContainerColor = Color(0xFF0F0F12)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("search_text_field")
            )
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PureWhite else Color(0xFF141416))
                        .border(1.dp, if (isSelected) PureWhite else Color(0xFF27272A), RoundedCornerShape(20.dp))
                        .clickable { onCategoryChange(cat) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("category_chip_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) PitchBlack else PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Grid Content
        if (filteredPhotos.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tr("photos_empty"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing filters or search query",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("photos_grid")
            ) {
                itemsIndexed(filteredPhotos) { index, photo ->
                    PhotoGridItem(
                        photo = photo,
                        onClick = {
                            val originalIndex = photos.indexOfFirst { it.id == photo.id }
                            onPhotoClick(if (originalIndex >= 0) originalIndex else index)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoGridItem(
    photo: PhotoItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF1E1E24), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("photo_item_${photo.id}")
    ) {
        PhotoCanvasImage(
            photoId = photo.id,
            modifier = Modifier.fillMaxSize()
        )

        // Favorite Indicator
        if (photo.isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = PureWhite,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Sync Status Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (photo.isCloudSynced) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                contentDescription = if (photo.isCloudSynced) "Cloud Synced" else "Sync Pending",
                tint = if (photo.isCloudSynced) Color(0xFF10B981) else Color(0xFFA1A1AA),
                modifier = Modifier.size(12.dp)
            )
        }

        // Detected face name chip on bottom left
        if (photo.faceNames.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = photo.faceNames.split(",")[0],
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
