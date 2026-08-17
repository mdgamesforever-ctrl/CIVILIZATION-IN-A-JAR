package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billing.BillingConnectionStatus
import com.example.data.CosmeticJarTheme
import com.example.data.GemPack
import com.example.data.GemStoreItem
import com.example.data.PlayerProfileEntity
import com.example.data.StoreDefinitions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    profile: PlayerProfileEntity?,
    billingStatus: BillingConnectionStatus,
    isPurchasePending: Boolean,
    billingMessage: String?,
    onBuyRealMoneyPack: (Activity, GemPack) -> Unit,
    onBuyGemStoreItem: (GemStoreItem) -> Unit,
    onSelectJarTheme: (String) -> Unit,
    onRetryBillingConnection: () -> Unit,
    getLocalizedPrice: (GemPack) -> String,
    onBackToJar: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Buy Gems, 1: Power-Ups & Cosmetics

    val gems = profile?.gems ?: 0L
    val speedUpUntil = profile?.speedUpActiveUntil ?: 0L
    val insuranceCount = profile?.extinctionInsuranceCount ?: 0
    val selectedTheme = profile?.selectedJarTheme ?: "default"
    val unlockedThemes = remember(profile?.unlockedJarThemesJson) {
        StoreDefinitions.parseUnlockedThemes(profile?.unlockedJarThemesJson ?: "[\"default\"]")
    }

    // Real-time speed-up countdown ticker
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(speedUpUntil) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }
    val speedUpRemainingMs = (speedUpUntil - currentTimeMillis).coerceAtLeast(0L)
    val isSpeedUpActive = speedUpRemainingMs > 0L

    val infiniteTransition = rememberInfiniteTransition(label = "gem_glow")
    val gemPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gem_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0B1A),
                        Color(0xFF140D24),
                        Color(0xFF17101E)
                    )
                )
            )
            .testTag("store_screen")
    ) {
        // TOP HEADER BAR
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("store_back_button")
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
                        text = "Cosmic Store",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Gems, Boosters & Jar Skins",
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            // PERSISTENT GEM COUNT PILL
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x3300E5FF))
                    .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = gemPulse), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("store_gem_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💎", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = gems.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF80D8FF)
                )
            }
        }

        // ACTIVE BOOST STATUS BANNERS (If active)
        if (isSpeedUpActive || insuranceCount > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isSpeedUpActive) {
                    val minutes = speedUpRemainingMs / 60000L
                    val seconds = (speedUpRemainingMs % 60000L) / 1000L
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x3300E676))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color(0xFF69F0AE),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2x POPULATION SPEED ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF69F0AE)
                            )
                        }
                        Text(
                            text = "${minutes}m ${seconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (insuranceCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x3329B6F6))
                            .border(1.dp, Color(0xFF29B6F6), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF81D4FA),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Extinction Insurance Active ($insuranceCount ${if (insuranceCount == 1) "Shield" else "Shields"} ready to block cataclysms)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE1F5FE)
                        )
                    }
                }
            }
        }

        // STORE TABS: Buy Gems vs Spend Gems
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0x22000000),
            contentColor = Color(0xFF00E5FF),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF00E5FF)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💎", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buy Gems",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) Color(0xFF00E5FF) else Color(0xFF90A4AE)
                        )
                    }
                },
                modifier = Modifier.testTag("tab_buy_gems")
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Power-Ups & Skins",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color(0xFF00E5FF) else Color(0xFF90A4AE)
                        )
                    }
                },
                modifier = Modifier.testTag("tab_spend_gems")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TAB CONTENT
        if (selectedTab == 0) {
            // TAB 0: BUY GEMS (Google Play Billing)
            BuyGemsSection(
                billingStatus = billingStatus,
                isPurchasePending = isPurchasePending,
                billingMessage = billingMessage,
                onRetryBillingConnection = onRetryBillingConnection,
                getLocalizedPrice = getLocalizedPrice,
                onBuyPack = { pack ->
                    if (activity != null) {
                        onBuyRealMoneyPack(activity, pack)
                    }
                }
            )
        } else {
            // TAB 1: POWER-UPS & COSMETICS (Spend Gems)
            SpendGemsSection(
                currentGems = gems,
                isPurchasePending = isPurchasePending,
                selectedTheme = selectedTheme,
                unlockedThemes = unlockedThemes,
                onBuyItem = onBuyGemStoreItem,
                onSelectTheme = onSelectJarTheme
            )
        }
    }
}

@Composable
private fun BuyGemsSection(
    billingStatus: BillingConnectionStatus,
    isPurchasePending: Boolean,
    billingMessage: String?,
    onRetryBillingConnection: () -> Unit,
    getLocalizedPrice: (GemPack) -> String,
    onBuyPack: (GemPack) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Billing Banner
            if (billingStatus == BillingConnectionStatus.DISCONNECTED || billingStatus == BillingConnectionStatus.ERROR) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FFB300)),
                    border = BorderStroke(1.dp, Color(0xFFFFB300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Google Play Store",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "Store preview mode active. Tap retry to reconnect.",
                                fontSize = 10.sp,
                                color = Color(0xFFFFECB3)
                            )
                        }
                        IconButton(
                            onClick = onRetryBillingConnection,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "REAL-MONEY GEM PACKS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF80D8FF),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(StoreDefinitions.GEM_PACKS) { pack ->
            GemPackCard(
                pack = pack,
                priceString = getLocalizedPrice(pack),
                isPending = isPurchasePending,
                onBuy = { onBuyPack(pack) }
            )
        }

        item {
            // Info text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒 Secured via Google Play In-App Billing",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
                Text(
                    text = "Purchased Gems persist permanently through all resets & extinctions.",
                    fontSize = 10.sp,
                    color = Color(0xFF546E7A),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun GemPackCard(
    pack: GemPack,
    priceString: String,
    isPending: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gem_pack_${pack.productId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B162B)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Color(0x6600E5FF),
                    Color(0x33FFFFFF),
                    Color(0x2200E5FF)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Gem Icon & Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x3300E5FF))
                        .border(1.dp, Color(0x6600E5FF), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = pack.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${pack.gemAmount} Gems",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        pack.badge?.let { badge ->
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE040FB))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = pack.title,
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Price & Buy Button
            Button(
                onClick = onBuy,
                enabled = !isPending,
                modifier = Modifier
                    .height(42.dp)
                    .testTag("buy_btn_${pack.productId}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF0A0E24),
                    disabledContainerColor = Color(0x4400E5FF),
                    disabledContentColor = Color(0x88FFFFFF)
                )
            ) {
                if (isPending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = priceString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendGemsSection(
    currentGems: Long,
    isPurchasePending: Boolean,
    selectedTheme: String,
    unlockedThemes: Set<String>,
    onBuyItem: (GemStoreItem) -> Unit,
    onSelectTheme: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "⚡ SPEED-UPS & EMPOWERMENTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Power-Up Items
        items(StoreDefinitions.POWER_UP_ITEMS) { item ->
            val canAfford = currentGems >= item.gemCost
            PowerUpStoreCard(
                item = item,
                canAfford = canAfford,
                isPending = isPurchasePending,
                onBuy = { onBuyItem(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🎨 COSMETIC JAR SKINS & THEMES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE040FB),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Cosmetic Themes
        items(StoreDefinitions.COSMETIC_THEMES) { theme ->
            val isUnlocked = unlockedThemes.contains(theme.id)
            val isEquipped = selectedTheme == theme.id
            val canAfford = currentGems >= theme.gemCost

            CosmeticThemeCard(
                theme = theme,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = canAfford,
                isPending = isPurchasePending,
                onEquip = { onSelectTheme(theme.id) },
                onUnlock = {
                    onBuyItem(
                        GemStoreItem(
                            id = "unlock_${theme.id}",
                            type = com.example.data.GemPowerUpType.COSMETIC_THEME,
                            title = theme.name,
                            description = theme.description,
                            gemCost = theme.gemCost,
                            iconEmoji = theme.previewEmoji,
                            themeId = theme.id
                        )
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PowerUpStoreCard(
    item: GemStoreItem,
    canAfford: Boolean,
    isPending: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("store_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1626)),
        border = BorderStroke(1.dp, if (canAfford) Color(0x44FFD54F) else Color(0x22FFFFFF))
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        item.badge?.let {
                            Text(
                                text = it,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }

                // Cost Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (canAfford) Color(0x3300E5FF) else Color(0x33FF5252))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "💎", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.gemCost}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (canAfford) Color(0xFF80D8FF) else Color(0xFFFF8A80)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = Color(0xFFB0BEC5),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford && !isPending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("buy_item_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB300),
                    contentColor = Color(0xFF1E140D),
                    disabledContainerColor = Color(0x22FFFFFF),
                    disabledContentColor = Color(0x55FFFFFF)
                )
            ) {
                if (canAfford) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PURCHASE FOR ${item.gemCost} GEMS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Text(
                        text = "NEED ${item.gemCost} GEMS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CosmeticThemeCard(
    theme: CosmeticJarTheme,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    isPending: Boolean,
    onEquip: () -> Unit,
    onUnlock: () -> Unit
) {
    val themeColor = Color(theme.glowColorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_card_${theme.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1422)),
        border = BorderStroke(
            1.dp,
            if (isEquipped) themeColor else if (isUnlocked) Color(0x44FFFFFF) else Color(0x22FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(theme.primaryColorHex))
                        .border(1.5.dp, themeColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = theme.previewEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = theme.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (isEquipped) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(themeColor)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = theme.description,
                        fontSize = 10.sp,
                        color = Color(0xFF90A4AE),
                        lineHeight = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isEquipped -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, themeColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Equipped", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onEquip,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF), contentColor = Color.White),
                        modifier = Modifier.height(36.dp).testTag("equip_theme_${theme.id}")
                    ) {
                        Text(text = "Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = onUnlock,
                        enabled = canAfford && !isPending,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE040FB),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x22FFFFFF),
                            disabledContentColor = Color(0x55FFFFFF)
                        ),
                        modifier = Modifier.height(36.dp).testTag("unlock_theme_${theme.id}")
                    ) {
                        Text(text = "💎 ${theme.gemCost}", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
