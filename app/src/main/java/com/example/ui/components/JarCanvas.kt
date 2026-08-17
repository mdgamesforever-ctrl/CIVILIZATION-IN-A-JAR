package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Era
import com.example.data.ExtinctionType
import com.example.data.JarType
import com.example.ui.FloatingParticle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JarCanvas(
    modifier: Modifier = Modifier,
    jarType: JarType,
    eraIndex: Int,
    population: Double,
    jarTheme: String = "default",
    isExtinctionAnimating: Boolean = false,
    extinctionType: ExtinctionType? = null,
    floatingParticles: List<FloatingParticle> = emptyList(),
    shakeTrigger: Long = 0L
) {
    val era = Era.fromIndex(eraIndex)
    val infiniteTransition = rememberInfiniteTransition(label = "jar_anim")

    // Ambient floating time
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_float"
    )

    // Pulse for singularity
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Shake offset
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0L) {
            repeat(6) { i ->
                val dir = if (i % 2 == 0) 18f else -18f
                shakeOffset.animateTo(dir * (1f - (i / 6f)), tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .offset(x = shakeOffset.value.dp)
            .testTag("jar_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("jar_canvas")
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Jar bounding box
            val jarLeft = canvasWidth * 0.18f
            val jarRight = canvasWidth * 0.82f
            val jarTop = canvasHeight * 0.12f
            val jarBottom = canvasHeight * 0.88f
            val jarWidth = jarRight - jarLeft
            val jarHeight = jarBottom - jarTop

            // 1. Draw Kitchen Counter Surface
            drawCountertop(canvasWidth, canvasHeight)

            // 2. Draw Jar Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x77000000), Color(0x00000000)),
                    center = Offset(canvasWidth * 0.5f, jarBottom + 12.dp.toPx()),
                    radius = jarWidth * 0.65f
                ),
                topLeft = Offset(jarLeft - 20.dp.toPx(), jarBottom - 10.dp.toPx()),
                size = Size(jarWidth + 40.dp.toPx(), 45.dp.toPx())
            )

            // 3. Draw Jar Interior Atmosphere (Based on Era & JarType)
            drawJarInterior(
                jarLeft = jarLeft,
                jarTop = jarTop,
                jarWidth = jarWidth,
                jarHeight = jarHeight,
                era = era,
                jarType = jarType,
                isExtinction = isExtinctionAnimating,
                extinctionType = extinctionType
            )

            // 4. Draw Animated Population Entities
            drawPopulationEntities(
                jarLeft = jarLeft,
                jarTop = jarTop,
                jarWidth = jarWidth,
                jarHeight = jarHeight,
                eraIndex = eraIndex,
                population = population,
                timeDegree = floatAnim,
                pulse = pulseAnim,
                jarType = jarType
            )

            // 5. Draw Glass Reflections & Jar Outline
            drawGlassJarContainer(
                jarLeft = jarLeft,
                jarTop = jarTop,
                jarWidth = jarWidth,
                jarHeight = jarHeight,
                jarType = jarType,
                jarTheme = jarTheme
            )

            // 6. Draw Extinction Special Effects (if active)
            if (isExtinctionAnimating && extinctionType != null) {
                drawExtinctionOverlay(
                    jarLeft = jarLeft,
                    jarTop = jarTop,
                    jarWidth = jarWidth,
                    jarHeight = jarHeight,
                    type = extinctionType,
                    pulse = pulseAnim
                )
            }
        }

        // Floating Text Particles
        floatingParticles.forEach { particle ->
            Text(
                text = particle.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (particle.isPop) Color(0xFF69F0AE) else Color(0xFFFFD700),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = (particle.xOffsetNorm * 110).dp,
                        y = (-80).dp
                    )
            )
        }
    }
}

private fun DrawScope.drawCountertop(w: Float, h: Float) {
    // Warm basement countertop wood texture gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E140D),
                Color(0xFF2C1B10),
                Color(0xFF3E2718),
                Color(0xFF24150B)
            ),
            startY = h * 0.70f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.70f),
        size = Size(w, h * 0.30f)
    )

    // Wood grain lines
    drawLine(
        color = Color(0x334A2E1B),
        start = Offset(0f, h * 0.76f),
        end = Offset(w, h * 0.77f),
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = Color(0x221B0F07),
        start = Offset(0f, h * 0.85f),
        end = Offset(w, h * 0.84f),
        strokeWidth = 3.dp.toPx()
    )
}

private fun DrawScope.drawJarInterior(
    jarLeft: Float,
    jarTop: Float,
    jarWidth: Float,
    jarHeight: Float,
    era: Era,
    jarType: JarType,
    isExtinction: Boolean,
    extinctionType: ExtinctionType?
) {
    val eraPrimary = Color(era.primaryColorHex)
    val eraSecondary = Color(era.secondaryColorHex)

    val baseInteriorColor1 = when (jarType) {
        JarType.SPICE -> Color(0xFF4A3416) // Sandy amber
        JarType.JAM -> Color(0xFF4D1A25) // Berry swamp
        JarType.AQUARIUM -> Color(0xFF072E44) // Deep ocean
        JarType.TERRARIUM -> Color(0xFF103B1E) // Lush forest
        JarType.MASON -> eraPrimary
    }

    val atmosphereColors = if (isExtinction) {
        when (extinctionType) {
            ExtinctionType.NUCLEAR_WAR -> listOf(Color(0xFF888888), Color(0xFF222222))
            ExtinctionType.RESOURCE_COLLAPSE -> listOf(Color(0xFF555555), Color(0xFF333333))
            ExtinctionType.PANDEMIC -> listOf(Color(0xFF881122), Color(0xFF33050C))
            ExtinctionType.PEACEFUL_ASCENSION -> listOf(Color(0xFFFFF9E6), Color(0xFFFFD54F))
            else -> listOf(Color(0xFF3E2723), Color(0xFF1A0E0B))
        }
    } else {
        listOf(
            baseInteriorColor1.copy(alpha = 0.35f),
            eraSecondary.copy(alpha = 0.65f),
            baseInteriorColor1.copy(alpha = 0.85f)
        )
    }

    val interiorPath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    left = jarLeft + 4.dp.toPx(),
                    top = jarTop + 14.dp.toPx(),
                    right = jarLeft + jarWidth - 4.dp.toPx(),
                    bottom = jarTop + jarHeight - 4.dp.toPx()
                ),
                cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
            )
        )
    }

    drawPath(
        path = interiorPath,
        brush = Brush.verticalGradient(
            colors = atmosphereColors,
            startY = jarTop,
            endY = jarTop + jarHeight
        )
    )

    // Jar sediment / ground floor at bottom
    val groundColor = when (jarType) {
        JarType.SPICE -> Color(0xFFC29B62)
        JarType.JAM -> Color(0xFF5A1E2B)
        JarType.AQUARIUM -> Color(0xFFE0C097)
        JarType.TERRARIUM -> Color(0xFF2E4C1F)
        JarType.MASON -> when (era.index) {
            1, 2 -> Color(0xFF2D4436)
            3, 4 -> Color(0xFF5D4037)
            5, 6 -> Color(0xFF37474F)
            7, 8 -> Color(0xFF0D1B2A)
            else -> Color(0xFF2A0845)
        }
    }

    val groundPath = Path().apply {
        val groundTop = jarTop + jarHeight * 0.78f
        moveTo(jarLeft + 6.dp.toPx(), groundTop)
        quadraticTo(
            jarLeft + jarWidth * 0.5f, groundTop - 8.dp.toPx(),
            jarLeft + jarWidth - 6.dp.toPx(), groundTop
        )
        lineTo(jarLeft + jarWidth - 6.dp.toPx(), jarTop + jarHeight - 6.dp.toPx())
        lineTo(jarLeft + 6.dp.toPx(), jarTop + jarHeight - 6.dp.toPx())
        close()
    }

    drawPath(
        path = groundPath,
        color = groundColor
    )
}

private fun DrawScope.drawPopulationEntities(
    jarLeft: Float,
    jarTop: Float,
    jarWidth: Float,
    jarHeight: Float,
    eraIndex: Int,
    population: Double,
    timeDegree: Float,
    pulse: Float,
    jarType: JarType
) {
    val centerX = jarLeft + jarWidth * 0.5f
    val centerY = jarTop + jarHeight * 0.5f
    val groundY = jarTop + jarHeight * 0.78f

    // If early era (1-2) or Aquarium: Swimming Amoebas / Marine life
    if (eraIndex in 1..2 || jarType == JarType.AQUARIUM) {
        val count = (6 + (population.coerceAtMost(10000.0) / 400)).toInt().coerceIn(6, 28)
        for (i in 0 until count) {
            val angle = (timeDegree * (0.8f + i * 0.1f) + i * (360f / count)) * (Math.PI / 180f)
            val radX = (jarWidth * 0.35f) * (0.4f + (i % 5) * 0.12f)
            val radY = (jarHeight * 0.28f) * (0.3f + (i % 4) * 0.16f)

            val px = centerX + cos(angle).toFloat() * radX
            val py = centerY + sin(angle * 1.3).toFloat() * radY

            // Swimming organism
            val cellColor = if (i % 2 == 0) Color(0xFF00E5FF) else Color(0xFF69F0AE)
            drawCircle(
                color = cellColor.copy(alpha = 0.85f),
                radius = (3.5f + (i % 3) * 1.5f).dp.toPx(),
                center = Offset(px, py)
            )

            // Flagella / tail
            val tailX = px - cos(angle).toFloat() * 12.dp.toPx()
            val tailY = py - sin(angle).toFloat() * 12.dp.toPx()
            drawLine(
                color = cellColor.copy(alpha = 0.4f),
                start = Offset(px, py),
                end = Offset(tailX, tailY),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }

    // Era 3-4 (Tribal / Agricultural): Sediment huts, bonfires, micro fields
    if (eraIndex in 3..4 || jarType == JarType.TERRARIUM) {
        val hutCount = (4 + eraIndex * 2).coerceIn(4, 10)
        for (i in 0 until hutCount) {
            val hx = jarLeft + jarWidth * (0.15f + (i.toFloat() / (hutCount + 1)) * 0.70f)
            val hy = groundY - 4.dp.toPx()

            // Micro-hut
            val hutPath = Path().apply {
                moveTo(hx - 7.dp.toPx(), hy)
                lineTo(hx, hy - 12.dp.toPx())
                lineTo(hx + 7.dp.toPx(), hy)
                close()
            }
            drawPath(hutPath, color = Color(0xFFD7CCC8))

            // Fire glow in center
            if (i == hutCount / 2) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF7043), Color(0x00FF7043)),
                        center = Offset(hx + 12.dp.toPx(), hy - 4.dp.toPx()),
                        radius = 16.dp.toPx() * pulse
                    ),
                    radius = 16.dp.toPx() * pulse,
                    center = Offset(hx + 12.dp.toPx(), hy - 4.dp.toPx())
                )
            }
        }
    }

    // Era 5-6 (City / Industrial): Tiny skyscraper skyline & factory steam
    if (eraIndex in 5..6) {
        val bldgCount = 9
        for (i in 0 until bldgCount) {
            val bx = jarLeft + jarWidth * (0.12f + (i.toFloat() / (bldgCount - 1)) * 0.76f)
            val bldgH = (18 + (i * 7 % 22)).dp.toPx()
            val bldgW = 10.dp.toPx()
            val by = groundY - bldgH

            drawRect(
                color = if (eraIndex == 6) Color(0xFF3E2723) else Color(0xFF455A64),
                topLeft = Offset(bx - bldgW * 0.5f, by),
                size = Size(bldgW, bldgH)
            )

            // Tiny lit windows
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 1.2.dp.toPx(),
                center = Offset(bx, by + 6.dp.toPx())
            )

            // Steam chimney particles for Industrial
            if (eraIndex == 6 && i % 3 == 0) {
                val steamY = by - ((timeDegree * 1.5f + i * 20f) % (jarHeight * 0.35f))
                drawCircle(
                    color = Color(0x55E0E0E0),
                    radius = 4.5.dp.toPx(),
                    center = Offset(bx, steamY)
                )
            }
        }
    }

    // Era 7-8 (Digital / Space): Neon cyber grid, satellite orbits, rocket launches
    if (eraIndex in 7..8) {
        val nodeCount = 12
        for (i in 0 until nodeCount) {
            val nx = jarLeft + jarWidth * (0.2f + (i * 0.23f) % 0.60f)
            val ny = jarTop + jarHeight * (0.25f + (i * 0.19f) % 0.50f)

            drawCircle(
                color = if (eraIndex == 7) Color(0xFF00E5FF) else Color(0xFFBB86FC),
                radius = 3.dp.toPx(),
                center = Offset(nx, ny)
            )

            // Connecting matrix lines
            if (i > 0) {
                val prevX = jarLeft + jarWidth * (0.2f + ((i - 1) * 0.23f) % 0.60f)
                val prevY = jarTop + jarHeight * (0.25f + ((i - 1) * 0.19f) % 0.50f)
                drawLine(
                    color = Color(0x3300E5FF),
                    start = Offset(nx, ny),
                    end = Offset(prevX, prevY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Space satellites orbiting inner rim
        if (eraIndex >= 8) {
            val satAngle = (timeDegree * 1.2f) * (Math.PI / 180f)
            val satX = centerX + cos(satAngle).toFloat() * (jarWidth * 0.42f)
            val satY = centerY + sin(satAngle).toFloat() * (jarHeight * 0.38f)

            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(satX - 3.dp.toPx(), satY - 3.dp.toPx()),
                size = Size(6.dp.toPx(), 6.dp.toPx())
            )
            // Solar wings
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(satX - 7.dp.toPx(), satY),
                end = Offset(satX + 7.dp.toPx(), satY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }

    // Era 9 (Ascension): Singularity Radiant Core
    if (eraIndex == 9) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFFD700),
                    Color(0x99BB86FC),
                    Color(0x004A148C)
                ),
                center = Offset(centerX, centerY),
                radius = (jarWidth * 0.45f) * pulse
            ),
            radius = (jarWidth * 0.45f) * pulse,
            center = Offset(centerX, centerY)
        )

        // Rotating golden rays
        rotate(timeDegree, pivot = Offset(centerX, centerY)) {
            for (r in 0 until 8) {
                val rayAngle = (r * 45f) * (Math.PI / 180f)
                val rx = centerX + cos(rayAngle).toFloat() * (jarWidth * 0.40f)
                val ry = centerY + sin(rayAngle).toFloat() * (jarHeight * 0.35f)
                drawLine(
                    color = Color(0x55FFE082),
                    start = Offset(centerX, centerY),
                    end = Offset(rx, ry),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

private fun DrawScope.drawGlassJarContainer(
    jarLeft: Float,
    jarTop: Float,
    jarWidth: Float,
    jarHeight: Float,
    jarType: JarType,
    jarTheme: String = "default"
) {
    // Cosmetic Theme Ambient Glow Aura (if custom theme equipped)
    val themeGlowColor = when (jarTheme) {
        "theme_amber" -> Color(0xFFFFB74D)
        "theme_cyber" -> Color(0xFF00E5FF)
        "theme_emerald" -> Color(0xFF69F0AE)
        "theme_solar" -> Color(0xFFFF7043)
        else -> null
    }

    if (themeGlowColor != null) {
        val auraPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        left = jarLeft - 4.dp.toPx(),
                        top = jarTop + 6.dp.toPx(),
                        right = jarLeft + jarWidth + 4.dp.toPx(),
                        bottom = jarTop + jarHeight + 4.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(26.dp.toPx(), 26.dp.toPx())
                )
            )
        }
        drawPath(
            path = auraPath,
            color = themeGlowColor.copy(alpha = 0.35f),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
        )
    }

    // 1. Metal Lid / Screw Cap at top
    val lidHeight = 22.dp.toPx()
    val lidExtraWidth = 14.dp.toPx()
    val lidTop = jarTop - 6.dp.toPx()
    val lidLeft = jarLeft - lidExtraWidth * 0.5f
    val lidWidth = jarWidth + lidExtraWidth

    val lidBrush = Brush.linearGradient(
        colors = when {
            jarTheme == "theme_cyber" -> listOf(Color(0xFF00E5FF), Color(0xFFE040FB), Color(0xFF00E5FF))
            jarTheme == "theme_solar" -> listOf(Color(0xFFFF5722), Color(0xFFFFCC80), Color(0xFFBF360C))
            jarTheme == "theme_amber" -> listOf(Color(0xFFFFB300), Color(0xFFFFE082), Color(0xFFFF8F00))
            jarTheme == "theme_emerald" -> listOf(Color(0xFF00C853), Color(0xFFA5D6A7), Color(0xFF1B5E20))
            jarType == JarType.SPICE -> listOf(Color(0xFF8D6E63), Color(0xFFD7CCC8), Color(0xFF5D4037))
            jarType == JarType.JAM -> listOf(Color(0xFFD32F2F), Color(0xFFFFCDD2), Color(0xFFB71C1C))
            jarType == JarType.TERRARIUM -> listOf(Color(0xFF3E2723), Color(0xFF8D6E63), Color(0xFF1B0000))
            else -> listOf(Color(0xFF78909C), Color(0xFFCFD8DC), Color(0xFF455A64), Color(0xFF37474F))
        },
        start = Offset(lidLeft, lidTop),
        end = Offset(lidLeft + lidWidth, lidTop)
    )

    drawRoundRect(
        brush = lidBrush,
        topLeft = Offset(lidLeft, lidTop),
        size = Size(lidWidth, lidHeight),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    // Screw ridges on lid
    drawLine(
        color = Color(0x44000000),
        start = Offset(lidLeft, lidTop + lidHeight * 0.5f),
        end = Offset(lidLeft + lidWidth, lidTop + lidHeight * 0.5f),
        strokeWidth = 1.5.dp.toPx()
    )

    // 2. Glass Jar Body Outline
    val jarOutlinePath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    left = jarLeft,
                    top = jarTop + 10.dp.toPx(),
                    right = jarLeft + jarWidth,
                    bottom = jarTop + jarHeight
                ),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )
        )
    }

    // Glass stroke
    drawPath(
        path = jarOutlinePath,
        color = Color(0x99FFFFFF),
        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // 3. Glass Refraction Highlight Curves (left reflection streak)
    val highlightPath = Path().apply {
        moveTo(jarLeft + 12.dp.toPx(), jarTop + 24.dp.toPx())
        lineTo(jarLeft + 12.dp.toPx(), jarTop + jarHeight - 24.dp.toPx())
    }
    drawPath(
        path = highlightPath,
        color = Color(0x66FFFFFF),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )

    // Secondary subtle right reflection
    val rightHighlightPath = Path().apply {
        moveTo(jarLeft + jarWidth - 14.dp.toPx(), jarTop + 30.dp.toPx())
        lineTo(jarLeft + jarWidth - 14.dp.toPx(), jarTop + jarHeight - 40.dp.toPx())
    }
    drawPath(
        path = rightHighlightPath,
        color = Color(0x22FFFFFF),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawExtinctionOverlay(
    jarLeft: Float,
    jarTop: Float,
    jarWidth: Float,
    jarHeight: Float,
    type: ExtinctionType,
    pulse: Float
) {
    val centerX = jarLeft + jarWidth * 0.5f
    val centerY = jarTop + jarHeight * 0.5f

    when (type) {
        ExtinctionType.METEOR_IMPACT -> {
            // Cracked glass spiderweb pattern
            val crackColor = Color(0xEEFFFFFF)
            val strokeW = 2.5.dp.toPx()
            drawLine(crackColor, Offset(centerX, centerY), Offset(jarLeft + 10.dp.toPx(), jarTop + 30.dp.toPx()), strokeW)
            drawLine(crackColor, Offset(centerX, centerY), Offset(jarLeft + jarWidth - 10.dp.toPx(), jarTop + 50.dp.toPx()), strokeW)
            drawLine(crackColor, Offset(centerX, centerY), Offset(centerX - 30.dp.toPx(), jarTop + jarHeight - 15.dp.toPx()), strokeW)
            drawLine(crackColor, Offset(centerX, centerY), Offset(centerX + 40.dp.toPx(), jarTop + jarHeight - 15.dp.toPx()), strokeW)
            drawCircle(Color(0xCCFFFFFF), radius = 12.dp.toPx(), center = Offset(centerX, centerY))
        }
        ExtinctionType.NUCLEAR_WAR -> {
            // Bright atomic mushroom flash
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFF9800), Color(0x00000000)),
                    center = Offset(centerX, centerY),
                    radius = (jarWidth * 0.6f) * pulse
                ),
                radius = (jarWidth * 0.6f) * pulse,
                center = Offset(centerX, centerY)
            )
        }
        ExtinctionType.PANDEMIC -> {
            // Red contagion biohazard spore clouds
            for (p in 0 until 16) {
                val angle = (p * 22.5f) * (Math.PI / 180f)
                val dist = (jarWidth * 0.35f) * ((p % 3 + 1) * 0.3f)
                val px = centerX + cos(angle).toFloat() * dist
                val py = centerY + sin(angle).toFloat() * dist
                drawCircle(
                    color = Color(0xCCFF1744),
                    radius = 8.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
        ExtinctionType.PEACEFUL_ASCENSION -> {
            // Divine ascension white & gold rayburst
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0x99FFD700), Color(0x00000000)),
                    center = Offset(centerX, centerY),
                    radius = jarWidth * 0.8f
                ),
                topLeft = Offset(jarLeft, jarTop),
                size = Size(jarWidth, jarHeight)
            )
        }
        ExtinctionType.RESOURCE_COLLAPSE -> {
            // Desaturated gray overlay
            drawRect(
                color = Color(0x88424242),
                topLeft = Offset(jarLeft, jarTop),
                size = Size(jarWidth, jarHeight)
            )
        }
    }
}
