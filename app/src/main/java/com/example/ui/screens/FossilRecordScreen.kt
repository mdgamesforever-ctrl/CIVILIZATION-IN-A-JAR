package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementCatalog
import com.example.data.AchievementCategory
import com.example.data.AchievementEntity
import com.example.data.CivilizationHistoryEntity
import com.example.data.FossilBranch
import com.example.data.FossilUpgradeDef
import com.example.data.FossilUpgradeEntity
import com.example.data.UpgradeCatalog
import com.example.util.NumberFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

@Composable
fun FossilRecordScreen(
    fossilDust: Double,
    fossilUpgrades: List<FossilUpgradeEntity>,
    civilizationHistory: List<CivilizationHistoryEntity>,
    unlockedAchievements: List<AchievementEntity> = emptyList(),
    greatResetMultiplier: Double = 1.0,
    passiveDustRatePerMin: Double = 0.0,
    onBuyFossilUpgrade: (FossilUpgradeDef) -> Unit,
    onBackToJar: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val upgradeLevels = fossilUpgrades.associate { it.upgradeId to it.level }
    val unlockedIds = unlockedAchievements.map { it.achievementId }.toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141018))
            .testTag("fossil_record_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToJar,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("fossil_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "THE FOSSIL RECORD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (passiveDustRatePerMin > 0) "+${NumberFormatter.format(passiveDustRatePerMin)}/min idle synthesis" else "Permanent Prestige Sanctuary",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB74D)
                    )
                }
            }

            // Fossil Dust Balance
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFB74D))
                    .border(1.dp, Color(0x66FFB74D), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💎", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = NumberFormatter.format(fossilDust),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        // Tab Row (Upgrades vs Milestones vs History)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E1A24),
            contentColor = Color(0xFFFFB74D),
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upgrades", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Milestones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Museum", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // Prestige Upgrades Tree Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (greatResetMultiplier > 1.0) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF261933))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚡", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Great Reset Bonus Active",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                        Text(
                                            text = "Future Fossil Dust gains are multiplied by ${String.format("%.2f", greatResetMultiplier)}x!",
                                            fontSize = 11.sp,
                                            color = Color(0xFFCFD8DC)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    FossilBranch.entries.forEach { branch ->
                        val branchUpgrades = UpgradeCatalog.FOSSIL_UPGRADES.filter { it.branch == branch }

                        item {
                            Text(
                                text = branch.displayName.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFCC80),
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        items(branchUpgrades, key = { it.id }) { upg ->
                            val currentLevel = upgradeLevels[upg.id] ?: 0
                            val isMaxed = currentLevel >= upg.maxLevel
                            val cost = upg.baseCostFD * (upg.costMultiplier.pow(currentLevel.toDouble()))
                            val canAfford = fossilDust >= cost && !isMaxed

                            FossilUpgradeCard(
                                upgrade = upg,
                                currentLevel = currentLevel,
                                isMaxed = isMaxed,
                                cost = cost,
                                canAfford = canAfford,
                                onBuy = { onBuyFossilUpgrade(upg) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            1 -> {
                // Milestones & Achievements Tab
                val totalAchievements = AchievementCatalog.ACHIEVEMENTS.size
                val unlockedCount = unlockedAchievements.size

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1829))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Civilization Milestones",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                    Text(
                                        text = "Unlock achievements to earn bonus Fossil Dust",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB0BEC5)
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x33FFD54F))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$unlockedCount / $totalAchievements",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFFD54F)
                                    )
                                }
                            }
                        }
                    }

                    AchievementCategory.entries.forEach { category ->
                        val catAchievements = AchievementCatalog.ACHIEVEMENTS.filter { it.category == category }
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            ) {
                                Text(text = category.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF80D8FF)
                                )
                            }
                        }

                        items(catAchievements, key = { it.id }) { ach ->
                            val isUnlocked = unlockedIds.contains(ach.id)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUnlocked) Color(0xFF241B33) else Color(0xFF18151E)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isUnlocked) Color(0x66FFD54F) else Color(0x22FFFFFF)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isUnlocked) Color(0x33FFD54F) else Color(0x22FFFFFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isUnlocked) ach.icon else "🔒",
                                            fontSize = if (isUnlocked) 22.sp else 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ach.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnlocked) Color.White else Color(0xFF78909C)
                                        )
                                        Text(
                                            text = ach.description,
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) Color(0xFFB0BEC5) else Color(0xFF546E7A)
                                        )
                                        if (ach.rewardFossilDust > 0.0) {
                                            Text(
                                                text = "+${NumberFormatter.format(ach.rewardFossilDust)} Fossil Dust Reward",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color(0xFF00E5FF) else Color(0xFF00838F)
                                            )
                                        }
                                    }

                                    if (isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Unlocked",
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }

            else -> {
                // History Museum Tab
                if (civilizationHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🏺", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Extinct Civilizations Yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "When your first jar inevitably collapses or ascends, its memory, era reached, and fossil yield will be permanently cataloged here.",
                                fontSize = 13.sp,
                                color = Color(0xFFB0BEC5),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(civilizationHistory.reversed(), key = { it.id }) { entry ->
                            HistoryCard(entry = entry)
                        }
                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FossilUpgradeCard(
    upgrade: FossilUpgradeDef,
    currentLevel: Int,
    isMaxed: Boolean,
    cost: Double,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fossil_upgrade_${upgrade.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMaxed) Color(0xFF1E281E) else Color(0xFF1C1822)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMaxed) Color(0xFF4CAF50) else if (canAfford) Color(0xFFFFB74D) else Color(0x33FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF37474F), Color(0xFF263238))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = upgrade.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = upgrade.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Level $currentLevel / ${upgrade.maxLevel}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isMaxed) Color(0xFF81C784) else Color(0xFFFFB74D)
                        )
                    }
                }

                if (isMaxed) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x334CAF50))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "MAXED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = upgrade.description,
                fontSize = 12.sp,
                color = Color(0xFFCFD8DC),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isMaxed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💎", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${NumberFormatter.format(cost)} FD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) Color(0xFFFFD54F) else Color(0xFFEF5350)
                        )
                    }
                } else {
                    Text(
                        text = "Permanent Mastery",
                        fontSize = 12.sp,
                        color = Color(0xFF81C784),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onBuy,
                    enabled = canAfford && !isMaxed,
                    modifier = Modifier.testTag("buy_fossil_upgrade_${upgrade.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8F00),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0x33455A64),
                        disabledContentColor = Color(0x66FFFFFF)
                    )
                ) {
                    Text(text = "Upgrade", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(entry: CivilizationHistoryEntity) {
    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(entry.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${entry.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A22)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${entry.jarName} • ${entry.eraName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = Color(0xFF90A4AE)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Extinction: ${entry.extinctionType}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF8A80)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+${NumberFormatter.format(entry.fossilDustEarned)} FD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Peak: ${NumberFormatter.format(entry.peakPopulation)} POP",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5)
                )
                Text(
                    text = "Survived: ${NumberFormatter.formatYears(entry.yearsSurvived)}",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5)
                )
            }
        }
    }
}
