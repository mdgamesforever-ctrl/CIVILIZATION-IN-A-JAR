package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.North
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Era
import com.example.util.NumberFormatter

/**
 * A dedicated Compose UI component that visualizes the current era of the civilization,
 * shows evolutionary epoch milestones, and prominently displays total accumulated fossil dust prestige.
 */
@Composable
fun EraPrestigeVisualizer(
    eraIndex: Int,
    population: Double,
    fossilDust: Double,
    modifier: Modifier = Modifier,
    inGameYears: Double? = null,
    globalPrestigeMultiplier: Double = 1.0,
    canAdvanceEra: Boolean = false,
    canAdvanceEraWithFossilDust: Boolean = false,
    onAdvanceEra: (() -> Unit)? = null,
    onAdvanceEraWithFossilDust: (() -> Unit)? = null,
    onOpenFossilRecord: (() -> Unit)? = null
) {
    val currentEra = Era.fromIndex(eraIndex)
    val nextEra = if (eraIndex < 9) Era.fromIndex(eraIndex + 1) else null
    val totalEras = Era.entries.size

    val primaryEraColor = Color(currentEra.primaryColorHex)
    val secondaryEraColor = Color(currentEra.secondaryColorHex)

    // Animated glow transition for active era highlight
    val infiniteTransition = rememberInfiniteTransition(label = "era_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val progress = if (nextEra != null && nextEra.requiredPopulation > 0) {
        (population / nextEra.requiredPopulation).toFloat().coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "era_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("era_prestige_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191522)),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    secondaryEraColor.copy(alpha = 0.6f),
                    Color(0xFFFFD54F).copy(alpha = 0.4f),
                    Color(0x22FFFFFF)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryEraColor.copy(alpha = 0.35f),
                            Color(0xFF14101A)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // TOP ROW: Current Era Tag + In-Game Years + Total Accumulated Fossil Dust Prestige
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Era Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(secondaryEraColor.copy(alpha = 0.18f))
                        .border(1.dp, secondaryEraColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(secondaryEraColor.copy(alpha = pulseGlow))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ERA $eraIndex OF $totalEras",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = secondaryEraColor,
                        letterSpacing = 1.sp
                    )
                }

                // In-Game Years (if provided)
                if (inGameYears != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33000000))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Time",
                            tint = Color(0xFFFFCC80),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = NumberFormatter.formatYears(inGameYears),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC80)
                        )
                    }
                }

                // TOTAL ACCUMULATED FOSSIL DUST PRESTIGE DISPLAY
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (onOpenFossilRecord != null) {
                                Modifier.clickable(onClick = onOpenFossilRecord)
                            } else Modifier
                        )
                        .testTag("fossil_dust_prestige_display"),
                    color = Color(0xFF2A1F10),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0x66FFB74D))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💎", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "FOSSIL DUST",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFB74D),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = NumberFormatter.format(fossilDust),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ERA TITLE & SUBTITLE LORE
            Column {
                Text(
                    text = currentEra.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("current_era_title")
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentEra.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 9-EPOCH EVOLUTIONARY STEPPER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (era in Era.entries) {
                    val isPast = era.index < eraIndex
                    val isCurrent = era.index == eraIndex
                    val isFuture = era.index > eraIndex

                    val stepColor = when {
                        isCurrent -> secondaryEraColor
                        isPast -> Color(0xFF66BB6A)
                        else -> Color(0x33FFFFFF)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCurrent) 6.dp else 4.dp)
                            .padding(horizontal = 1.5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isCurrent) {
                                    stepColor.copy(alpha = pulseGlow)
                                } else stepColor
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PROGRESS TOWARDS NEXT ERA OR ASCENSION
            if (nextEra != null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next: ${nextEra.title}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF90A4AE)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% (${NumberFormatter.format(population)} / ${NumberFormatter.format(nextEra.requiredPopulation)})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAdvanceEra) Color(0xFF69F0AE) else Color(0xFFCFD8DC)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (canAdvanceEra) Color(0xFF69F0AE) else secondaryEraColor,
                        trackColor = Color(0x22FFFFFF),
                        strokeCap = StrokeCap.Round
                    )

                    // Fossil Dust Milestone Sub-indicator
                    if (nextEra.requiredFossilDust > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💎 Fossil Milestone: ${NumberFormatter.format(nextEra.requiredFossilDust)} FD",
                                fontSize = 9.sp,
                                color = if (canAdvanceEraWithFossilDust) Color(0xFFFFD54F) else Color(0xFFB0BEC5)
                            )
                            if (canAdvanceEraWithFossilDust) {
                                Text(
                                    text = "⚡ Milestone Met!",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                    }
                }

                // ERA ADVANCEMENT BUTTON (When population requirements met)
                if (canAdvanceEra && onAdvanceEra != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onAdvanceEra,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("advance_era_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = secondaryEraColor,
                            contentColor = Color(0xFF141018)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.North,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADVANCE TO ${nextEra.title.uppercase()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                } else if (canAdvanceEraWithFossilDust && onAdvanceEraWithFossilDust != null) {
                    // FOSSIL DUST CATALYST MILESTONE EVOLUTION BUTTON
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onAdvanceEraWithFossilDust,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("fossil_advance_era_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB74D),
                            contentColor = Color(0xFF1E140D)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CATALYZE ERA WITH FOSSIL DUST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                // Max Era Achieved
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33BB86FC))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFE1BEE7),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Max Era Reached: Ready for Singularity Ascension!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE1BEE7)
                    )
                }
            }

            // PRESTIGE STATS FOOTER (Global multiplier & lore hook)
            if (globalPrestigeMultiplier > 1.0 || fossilDust > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x18000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Prestige Growth Multiplier: ${String.format(java.util.Locale.US, "%.1fx", globalPrestigeMultiplier)}",
                            fontSize = 10.sp,
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (onOpenFossilRecord != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onOpenFossilRecord)
                        ) {
                            Text(
                                text = "View Record",
                                fontSize = 10.sp,
                                color = Color(0xFF80D8FF),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF80D8FF),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
