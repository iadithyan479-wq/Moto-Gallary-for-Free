package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

enum class PhotoFilter {
    ORIGINAL,
    HIGH_CONTRAST_BW,
    NOIR,
    SHARP_SILVER,
    INVERT
}

@Composable
fun PhotoCanvasImage(
    photoId: String,
    filter: PhotoFilter = PhotoFilter.ORIGINAL,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFF0A0A0C))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (photoId) {
                "photo_1" -> drawArchitecture(w, h, filter)
                "photo_2" -> drawPortraitElena(w, h, filter)
                "photo_3" -> drawNightCity(w, h, filter)
                "photo_4" -> drawPortraitMarcus(w, h, filter)
                "photo_5" -> drawPortraitSophia(w, h, filter)
                "photo_6" -> drawMinimalHorizon(w, h, filter)
                "photo_7" -> drawKidsPlay(w, h, filter)
                "photo_8" -> drawVaultDocument(w, h, filter)
                "photo_9" -> drawVaultIdentity(w, h, filter)
                "photo_10" -> drawStreetShadow(w, h, filter)
                else -> drawGenericAbstract(w, h, photoId, filter)
            }
        }
    }
}

private fun DrawScope.drawArchitecture(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFF4F4F5) else Color(0xFF0F0F12)
    val lineCol = if (filter == PhotoFilter.INVERT) Color(0xFF18181B) else Color(0xFFE4E4E7)
    val accentCol = if (filter == PhotoFilter.INVERT) Color(0xFF71717A) else Color(0xFFA1A1AA)

    drawRect(bg)

    // Architectural geometric perspectives
    val path = Path().apply {
        moveTo(w * 0.1f, h)
        lineTo(w * 0.45f, h * 0.15f)
        lineTo(w * 0.55f, h * 0.15f)
        lineTo(w * 0.9f, h)
        close()
    }
    drawPath(path, Brush.verticalGradient(listOf(lineCol.copy(alpha = 0.8f), bg)))

    // Grid lines of facade
    for (i in 1..8) {
        val y = h * 0.2f + (h * 0.7f * i / 8f)
        drawLine(
            color = accentCol.copy(alpha = 0.4f),
            start = Offset(w * 0.2f, y),
            end = Offset(w * 0.8f, y),
            strokeWidth = 2f
        )
    }

    // High contrast diagonal shadow
    val shadowPath = Path().apply {
        moveTo(w * 0.5f, h * 0.15f)
        lineTo(w * 0.9f, h)
        lineTo(w * 0.5f, h)
        close()
    }
    drawPath(shadowPath, Color.Black.copy(alpha = 0.5f))
}

private fun DrawScope.drawPortraitElena(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFFAFAFA) else Color(0xFF09090B)
    val skin = if (filter == PhotoFilter.INVERT) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val rimLight = if (filter == PhotoFilter.INVERT) Color(0xFF000000) else Color(0xFFFFFFFF)

    drawRect(bg)

    // Studio rim lighting
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(rimLight.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(w * 0.65f, h * 0.35f),
            radius = w * 0.5f
        ),
        radius = w * 0.5f,
        center = Offset(w * 0.65f, h * 0.35f)
    )

    // Portrait head silhouette
    drawCircle(
        color = skin.copy(alpha = 0.9f),
        radius = w * 0.28f,
        center = Offset(w * 0.5f, h * 0.38f)
    )

    // Shoulders
    val shoulderPath = Path().apply {
        moveTo(w * 0.1f, h)
        cubicTo(w * 0.2f, h * 0.7f, w * 0.35f, h * 0.62f, w * 0.5f, h * 0.65f)
        cubicTo(w * 0.65f, h * 0.62f, w * 0.8f, h * 0.7f, w * 0.9f, h)
        close()
    }
    drawPath(shoulderPath, skin.copy(alpha = 0.75f))

    // High contrast studio catchlights in eyes
    drawCircle(rimLight, radius = 5f, center = Offset(w * 0.44f, h * 0.37f))
    drawCircle(rimLight, radius = 5f, center = Offset(w * 0.56f, h * 0.37f))
}

private fun DrawScope.drawNightCity(w: Float, h: Float, filter: PhotoFilter) {
    val sky = if (filter == PhotoFilter.INVERT) Color(0xFFE4E4E7) else Color(0xFF000000)
    val glow = if (filter == PhotoFilter.INVERT) Color(0xFF18181B) else Color(0xFFFFFFFF)

    drawRect(sky)

    // Night skyscrapers
    val buildingWidths = listOf(0.12f, 0.15f, 0.2f, 0.18f, 0.14f, 0.15f)
    var currentX = 0f
    val heights = listOf(0.55f, 0.7f, 0.4f, 0.82f, 0.6f, 0.45f)

    buildingWidths.forEachIndexed { i, bwRatio ->
        val bw = w * bwRatio
        val bh = h * heights[i % heights.size]
        drawRect(
            color = if (filter == PhotoFilter.INVERT) Color(0xFFD4D4D8) else Color(0xFF18181B),
            topLeft = Offset(currentX, h - bh),
            size = Size(bw, bh)
        )
        // Window dots
        for (wy in 1..8) {
            val winY = h - bh + (bh * wy / 10f)
            for (wx in 1..3) {
                val winX = currentX + (bw * wx / 4f)
                drawCircle(glow.copy(alpha = 0.6f), radius = 2f, center = Offset(winX, winY))
            }
        }
        currentX += bw
    }

    // Motorola Night Vision sensor glare reflection line
    drawLine(
        brush = Brush.horizontalGradient(listOf(Color.Transparent, glow.copy(alpha = 0.8f), Color.Transparent)),
        start = Offset(0f, h * 0.88f),
        end = Offset(w, h * 0.88f),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawPortraitMarcus(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFF4F4F5) else Color(0xFF121214)
    val fg = if (filter == PhotoFilter.INVERT) Color(0xFF09090B) else Color(0xFFE4E4E7)

    drawRect(bg)
    // Dynamic outdoor profile
    drawCircle(fg.copy(alpha = 0.85f), radius = w * 0.26f, center = Offset(w * 0.52f, h * 0.36f))

    val path = Path().apply {
        moveTo(w * 0.15f, h)
        lineTo(w * 0.35f, h * 0.65f)
        lineTo(w * 0.65f, h * 0.65f)
        lineTo(w * 0.85f, h)
        close()
    }
    drawPath(path, fg.copy(alpha = 0.7f))

    // Jawline shadow
    drawLine(
        color = Color.Black.copy(alpha = 0.4f),
        start = Offset(w * 0.4f, h * 0.45f),
        end = Offset(w * 0.6f, h * 0.45f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawPortraitSophia(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFE4E4E7) else Color(0xFF0B0B0E)
    val light = if (filter == PhotoFilter.INVERT) Color(0xFF18181B) else Color(0xFFFFFFFF)

    drawRect(bg)
    // Sunlight rays
    for (i in 0..4) {
        drawLine(
            color = light.copy(alpha = 0.15f),
            start = Offset(w * 0.1f * i, 0f),
            end = Offset(w * 0.3f * i, h),
            strokeWidth = 14f
        )
    }
    drawCircle(light.copy(alpha = 0.85f), radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.4f))
}

private fun DrawScope.drawMinimalHorizon(w: Float, h: Float, filter: PhotoFilter) {
    val sky = if (filter == PhotoFilter.INVERT) Color(0xFFF4F4F5) else Color(0xFF050505)
    val water = if (filter == PhotoFilter.INVERT) Color(0xFFE4E4E7) else Color(0xFF121214)
    val moon = if (filter == PhotoFilter.INVERT) Color(0xFF09090B) else Color(0xFFFFFFFF)

    drawRect(sky, size = Size(w, h * 0.55f))
    drawRect(water, topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f))

    // Crisp high contrast moon
    drawCircle(moon, radius = w * 0.08f, center = Offset(w * 0.7f, h * 0.3f))

    // Horizon line
    drawLine(moon.copy(alpha = 0.7f), start = Offset(0f, h * 0.55f), end = Offset(w, h * 0.55f), strokeWidth = 2f)

    // Moon water reflections
    for (i in 1..6) {
        val y = h * 0.56f + (h * 0.35f * i / 6f)
        val rw = w * 0.15f * (1f - i * 0.1f)
        drawLine(
            moon.copy(alpha = 0.4f),
            start = Offset(w * 0.7f - rw / 2, y),
            end = Offset(w * 0.7f + rw / 2, y),
            strokeWidth = 3f
        )
    }
}

private fun DrawScope.drawKidsPlay(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFFAFAFA) else Color(0xFF0D0D11)
    val fg = if (filter == PhotoFilter.INVERT) Color(0xFF18181B) else Color(0xFFE4E4E7)

    drawRect(bg)
    // Two kids silhouettes
    drawCircle(fg.copy(alpha = 0.85f), radius = w * 0.14f, center = Offset(w * 0.38f, h * 0.42f))
    drawCircle(fg.copy(alpha = 0.85f), radius = w * 0.12f, center = Offset(w * 0.62f, h * 0.46f))

    // Park hill
    val hill = Path().apply {
        moveTo(0f, h)
        quadraticTo(w * 0.5f, h * 0.6f, w, h)
        close()
    }
    drawPath(hill, fg.copy(alpha = 0.4f))
}

private fun DrawScope.drawVaultDocument(w: Float, h: Float, filter: PhotoFilter) {
    val bg = Color(0xFF08080A)
    drawRect(bg)

    // Document canvas
    val docRect = Size(w * 0.75f, h * 0.8f)
    val docTopLeft = Offset(w * 0.125f, h * 0.1f)
    drawRect(Color(0xFF1C1C21), topLeft = docTopLeft, size = docRect)
    drawRect(Color(0xFF3F3F46), topLeft = docTopLeft, size = docRect, style = Stroke(width = 2f))

    // Encrypted text lines
    for (i in 1..8) {
        val y = h * 0.18f + (h * 0.07f * i)
        val lineW = if (i % 3 == 0) w * 0.4f else w * 0.55f
        drawLine(
            Color(0xFFA1A1AA),
            start = Offset(w * 0.22f, y),
            end = Offset(w * 0.22f + lineW, y),
            strokeWidth = 4f
        )
    }

    // Vault watermark badge
    drawCircle(
        Color(0xFF10B981).copy(alpha = 0.3f),
        radius = w * 0.18f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
}

private fun DrawScope.drawVaultIdentity(w: Float, h: Float, filter: PhotoFilter) {
    val bg = Color(0xFF000000)
    drawRect(bg)

    // Biometric fingerprint lines simulation
    for (r in 1..7) {
        drawCircle(
            color = Color(0xFFE4E4E7).copy(alpha = 0.8f),
            radius = w * (0.05f * r),
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = 3f)
        )
    }
}

private fun DrawScope.drawStreetShadow(w: Float, h: Float, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFFFFFFF) else Color(0xFF121214)
    val dark = if (filter == PhotoFilter.INVERT) Color(0xFF000000) else Color(0xFF000000)
    val light = if (filter == PhotoFilter.INVERT) Color(0xFF3F3F46) else Color(0xFFE4E4E7)

    drawRect(bg)

    // Strong diagonal light & shadow
    val shadow = Path().apply {
        moveTo(0f, 0f)
        lineTo(w, h * 0.8f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(shadow, dark)

    // Railing stripes
    for (i in 0..5) {
        val x = w * (0.15f * i)
        drawLine(light.copy(alpha = 0.5f), start = Offset(x, 0f), end = Offset(x + w * 0.3f, h), strokeWidth = 3f)
    }
}

private fun DrawScope.drawGenericAbstract(w: Float, h: Float, seed: String, filter: PhotoFilter) {
    val bg = if (filter == PhotoFilter.INVERT) Color(0xFFF4F4F5) else Color(0xFF0B0B0E)
    val fg = if (filter == PhotoFilter.INVERT) Color(0xFF18181B) else Color(0xFFE4E4E7)
    drawRect(bg)
    drawCircle(fg.copy(alpha = 0.5f), radius = w * 0.3f, center = Offset(w * 0.5f, h * 0.5f))
}
