package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Era
import com.example.data.GameRepository
import com.example.data.JarType
import com.example.data.UpgradeCatalog
import com.example.ui.ActiveScreen
import com.example.ui.FloatingParticle
import com.example.ui.components.EraPrestigeVisualizer
import com.example.ui.components.JarCanvas
import com.example.util.NumberFormatter
import kotlinx.coroutines.launch

@Composable
fun MainJarScreen(
    jarType: JarType,
    eraIndex: Int,
    population: Double,
    organicMatter: Double,
    inGameYears: Double,
    natureMeter: Double,
    fossilDust: Double,
    gems: Long = 0L,
    speedUpActiveUntil: Long = 0L,
    jarTheme: String = "default",
    upgradeLevels: Map<String, Int>,
    fossilUpgradesMap: Map<String, Int>,
    floatingParticles: List<FloatingParticle>,
    shakeTrigger: Long,
    bannerMessage: String?,
    onJarTapped: () -> Unit,
    onAdvanceEra: () -> Unit,
    canAdvanceEra: Boolean,
    canAdvanceEraWithFossilDust: Boolean = false,
    onAdvanceEraWithFossilDust: (() -> Unit)? = null,
    onOpenShop: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenFossilRecord: () -> Unit,
    onOpenKitchenCounter: () -> Unit,
    onOpenJarSelectionMenu: () -> Unit = onOpenKitchenCounter,
    onOpenSettings: () -> Unit,
    onCollapseRequested: () -> Unit
) {
    val era = Era.fromIndex(eraIndex)
    val nextEra = if (eraIndex < 9) Era.fromIndex(eraIndex + 1) else null
    val coroutineScope = rememberCoroutineScope()
    val tapScale = remember { Animatable(1f) }

    // Calculate current pop per second
    val globalGrowthMult = GameRepository.calculateGlobalGrowthMultiplier(fossilUpgradesMap)
    val baseRate = GameRepository.calculateJarPopRate(eraIndex, upgradeLevels, jarType)
    var terrariumMult = 1.0
    if (jarType == JarType.TERRARIUM) {
        terrariumMult = 1.0 + (natureMeter / 25.0)
    }
    val effectivePopRate = baseRate * globalGrowthMult * jarType.growthMultiplier * terrariumMult
    val passiveOM = GameRepository.calculatePassiveOmPerSec(fossilUpgradesMap) * jarType.resourceMultiplier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141018),
                        Color(0xFF1B141C),
                        Color(0xFF1E140D)
                    )
                )
            )
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Status Header: Jar Selector & Fossil Dust Counter & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Jar Switcher Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(jarType.accentColorHex).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenJarSelectionMenu)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_jar_selector"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = jarType.emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = jarType.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (jarType.fossilDustMultiplier > 1.0) "✨ ${String.format("%.2f", jarType.fossilDustMultiplier)}x FD" else "✨ 1.0x FD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• Switch",
                            fontSize = 9.sp,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }

            // Fossil Dust & Gems Displays
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Fossil Dust Display
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFB74D))
                        .border(1.dp, Color(0x66FFB74D), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenFossilRecord)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("top_fossil_dust_badge"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = NumberFormatter.format(fossilDust),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F)
                    )
                }

                // Gems Display
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x3300E5FF))
                        .border(1.dp, Color(0x6600E5FF), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenStore)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("top_gems_badge"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💎", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$gems",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF80D8FF)
                    )
                }
            }

            // Settings & Collapse Buttons
            Row {
                IconButton(
                    onClick = onCollapseRequested,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FF5252))
                        .testTag("top_collapse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Collapse / Extinction",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("top_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Outcome Notification Banner
        AnimatedVisibility(visible = bannerMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xEE2E7D32))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bannerMessage ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Era & Prestige Visualizer Header Component
        EraPrestigeVisualizer(
            eraIndex = eraIndex,
            population = population,
            fossilDust = fossilDust,
            inGameYears = inGameYears,
            globalPrestigeMultiplier = globalGrowthMult,
            canAdvanceEra = canAdvanceEra,
            canAdvanceEraWithFossilDust = canAdvanceEraWithFossilDust,
            onAdvanceEra = onAdvanceEra,
            onAdvanceEraWithFossilDust = onAdvanceEraWithFossilDust,
            onOpenFossilRecord = onOpenFossilRecord,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Speed-Up Active Indicator
        val isSpeedUpActive = speedUpActiveUntil > System.currentTimeMillis()
        if (isSpeedUpActive) {
            val remainingSecs = ((speedUpActiveUntil - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
            val mins = remainingSecs / 60
            val secs = remainingSecs % 60
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFE040FB), Color(0xFFFF4081))))
                    .clickable(onClick = onOpenStore)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡ 2x Speed Surge Active (${mins}m ${secs}s left) — Tap for Store",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Jar Visual Simulation Canvas
        JarCanvas(
            modifier = Modifier.padding(vertical = 4.dp),
            jarType = jarType,
            eraIndex = eraIndex,
            population = population,
            jarTheme = jarTheme,
            floatingParticles = floatingParticles,
            shakeTrigger = shakeTrigger
        )

        // Large Readout Counters (Population & Organic Matter)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("main_counters_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x44000000)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Population Counter
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "POPULATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81D4FA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = NumberFormatter.format(population),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = NumberFormatter.formatRate(effectivePopRate),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF69F0AE)
                    )
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .width(1.dp)
                        .background(Color(0x33FFFFFF))
                )

                // Organic Matter Counter
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ORGANIC MATTER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC80),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = NumberFormatter.format(organicMatter),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = if (passiveOM > 0) "+${NumberFormatter.format(passiveOM)}/s" else "Tap jar",
                        fontSize = 11.sp,
                        color = Color(0xFFFFE082)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Glowing Tap Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .scale(tapScale.value)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF9800),
                            Color(0xFFFFB74D),
                            Color(0xFFFF9800)
                        )
                    )
                )
                .border(2.dp, Color(0xFFFFE082), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        coroutineScope.launch {
                            tapScale.animateTo(0.93f, tween(40))
                            tapScale.animateTo(1f, tween(60))
                        }
                        onJarTapped()
                    }
                )
                .testTag("tap_organic_matter_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Tap",
                    tint = Color(0xFF1E140A),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "TAP TO ADD ORGANIC MATTER",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E140A),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Fuels evolution & spawns micro-cells",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Navigation Tabs Row (Shop, Fossil Record, Store, Kitchen Counter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickNavCard(
                icon = Icons.Default.ShoppingCart,
                title = "Upgrades",
                subtitle = "Shop tech",
                badgeColor = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f),
                testTag = "nav_shop_button",
                onClick = onOpenShop
            )

            QuickNavCard(
                icon = Icons.Default.Science,
                title = "Fossils",
                subtitle = "Prestige",
                badgeColor = Color(0xFFFFD54F),
                modifier = Modifier.weight(1f),
                testTag = "nav_fossil_button",
                onClick = onOpenFossilRecord
            )

            QuickNavCard(
                icon = Icons.Default.AutoAwesome,
                title = "Store",
                subtitle = "💎 & Boosts",
                badgeColor = Color(0xFFE040FB),
                modifier = Modifier.weight(1f),
                testTag = "nav_store_button",
                onClick = onOpenStore
            )

            QuickNavCard(
                icon = Icons.Default.Widgets,
                title = "All Jars",
                subtitle = "5 worlds",
                badgeColor = Color(0xFF69F0AE),
                modifier = Modifier.weight(1f),
                testTag = "nav_counter_button",
                onClick = onOpenKitchenCounter
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickNavCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x33263238)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = badgeColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color(0xFFB0BEC5)
            )
        }
    }
}
