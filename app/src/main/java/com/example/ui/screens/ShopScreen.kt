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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Era
import com.example.data.PopulationUpgradeDef
import com.example.data.UpgradeCatalog
import com.example.util.NumberFormatter
import kotlin.math.pow

@Composable
fun ShopScreen(
    currentEraIndex: Int,
    population: Double,
    organicMatter: Double,
    upgradeLevels: Map<String, Int>,
    buyMultiplier: Int,
    onSetBuyMultiplier: (Int) -> Unit,
    onBuyUpgrade: (PopulationUpgradeDef) -> Unit,
    calculatePurchaseCount: (PopulationUpgradeDef, Int, Int) -> Int,
    onBackToJar: () -> Unit
) {
    var selectedEraTab by remember { mutableIntStateOf(currentEraIndex.coerceIn(1, 9)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121016))
            .testTag("shop_screen")
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
                        .testTag("shop_back_button")
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
                        text = "POPULATION UPGRADES",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Reset upon extinction",
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            // Buy Multiplier Switcher (1x, 10x, Max)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33000000))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(2.dp)
            ) {
                listOf(1, 10, 100).forEach { mult ->
                    val label = if (mult == 100) "MAX" else "${mult}x"
                    val isSelected = buyMultiplier == mult
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFFFB74D) else Color.Transparent)
                            .clickable { onSetBuyMultiplier(mult) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("buy_multiplier_${label}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF1E140A) else Color(0xFFB0BEC5)
                        )
                    }
                }
            }
        }

        // Live Currency Balance Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33263238))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "POP:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81D4FA))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = NumberFormatter.format(population),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "OM:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFCC80))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = NumberFormatter.format(organicMatter),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Era Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedEraTab - 1,
            containerColor = Color.Transparent,
            contentColor = Color(0xFFFFB74D),
            edgePadding = 16.dp,
            divider = {}
        ) {
            for (eIndex in 1..9) {
                val era = Era.fromIndex(eIndex)
                val isUnlocked = eIndex <= currentEraIndex
                Tab(
                    selected = selectedEraTab == eIndex,
                    onClick = { selectedEraTab = eIndex },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFF78909C),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "Era $eIndex",
                                fontSize = 12.sp,
                                fontWeight = if (selectedEraTab == eIndex) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedEraTab == eIndex) Color(0xFFFFB74D) else if (isUnlocked) Color(0xFFCFD8DC) else Color(0xFF78909C)
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val tabEra = Era.fromIndex(selectedEraTab)
        val upgradesForEra = UpgradeCatalog.POPULATION_UPGRADES.filter { it.eraIndex == selectedEraTab }
        val isEraUnlocked = selectedEraTab <= currentEraIndex

        if (!isEraUnlocked) {
            // Locked Era Notice
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF78909C),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${tabEra.title} Locked",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Grow your population to ${NumberFormatter.format(tabEra.requiredPopulation)} and evolve to unlock this tech tree.",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Upgrades List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(upgradesForEra, key = { it.id }) { upg ->
                    val currentLevel = upgradeLevels[upg.id] ?: 0
                    val canAffordCount = calculatePurchaseCount(upg, currentLevel, buyMultiplier)
                    val isPurchasable = canAffordCount > 0

                    // Calculate cost for 1x
                    val nextCostPOP = upg.baseCostPOP * (upg.costMultiplier.pow(currentLevel.toDouble()))
                    val nextCostOM = if (upg.baseCostOM > 0) upg.baseCostOM * (upg.costMultiplier.pow(currentLevel.toDouble())) else 0.0

                    UpgradeCard(
                        upgrade = upg,
                        currentLevel = currentLevel,
                        costPOP = nextCostPOP,
                        costOM = nextCostOM,
                        canAffordCount = canAffordCount,
                        isPurchasable = isPurchasable,
                        buyMultiplier = buyMultiplier,
                        onBuy = { onBuyUpgrade(upg) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    upgrade: PopulationUpgradeDef,
    currentLevel: Int,
    costPOP: Double,
    costOM: Double,
    canAffordCount: Int,
    isPurchasable: Boolean,
    buyMultiplier: Int,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upgrade_card_${upgrade.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C24)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPurchasable) Color(0x66FFB74D) else Color(0x22FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x3337474F)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = upgrade.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = upgrade.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (currentLevel > 0) {
                        Text(
                            text = "Lv.$currentLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFB74D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = upgrade.description,
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Cost display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${NumberFormatter.format(costPOP)} POP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81D4FA)
                    )
                    if (costOM > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+${NumberFormatter.format(costOM)} OM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Buy Button
            Button(
                onClick = onBuy,
                enabled = isPurchasable,
                modifier = Modifier
                    .height(44.dp)
                    .testTag("buy_button_${upgrade.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB74D),
                    contentColor = Color(0xFF1E140A),
                    disabledContainerColor = Color(0x33455A64),
                    disabledContentColor = Color(0x66FFFFFF)
                )
            ) {
                Text(
                    text = if (buyMultiplier > 1 && canAffordCount > 1) "Buy +$canAffordCount" else "Buy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
