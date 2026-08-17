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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.JarStateEntity
import com.example.data.JarType
import com.example.util.NumberFormatter

@Composable
fun KitchenCounterScreen(
    activeJarId: String,
    allJars: List<JarStateEntity>,
    fossilDust: Double,
    onSelectJar: (String) -> Unit,
    onUnlockJar: (JarType) -> Unit,
    onBackToJar: () -> Unit
) {
    val jarStatesMap = allJars.associateBy { it.jarId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141018))
            .testTag("kitchen_counter_screen")
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
                        .testTag("counter_back_button")
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
                        text = "THE KITCHEN COUNTER",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "5 Parallel Miniature Worlds",
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            // Fossil Dust Badge
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(JarType.entries, key = { it.id }) { jarType ->
                val state = jarStatesMap[jarType.id]
                val isUnlocked = state?.isUnlocked == true
                val isActive = jarType.id == activeJarId
                val canAffordUnlock = fossilDust >= jarType.unlockCostFD

                JarOverviewCard(
                    jarType = jarType,
                    state = state,
                    isUnlocked = isUnlocked,
                    isActive = isActive,
                    canAffordUnlock = canAffordUnlock,
                    onSelect = { onSelectJar(jarType.id) },
                    onUnlock = { onUnlockJar(jarType) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun JarOverviewCard(
    jarType: JarType,
    state: JarStateEntity?,
    isUnlocked: Boolean,
    isActive: Boolean,
    canAffordUnlock: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val era = state?.let { Era.fromIndex(it.currentEraIndex) } ?: Era.PRIMORDIAL_SOUP

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("jar_card_${jarType.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF261D2E) else Color(0xFF1B1720)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isActive) Color(0xFFFFB74D) else if (isUnlocked) Color(0x33FFFFFF) else Color(0x11FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(jarType.accentColorHex).copy(alpha = 0.25f))
                            .border(1.dp, Color(jarType.accentColorHex).copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = jarType.emoji,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = jarType.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF2E7D32))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isUnlocked) "Era ${era.index}: ${era.title} (${jarType.biomeName})" else "Locked: ${jarType.biomeName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUnlocked) Color(era.secondaryColorHex) else Color(0xFF78909C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fossil Dust Bonus Banner
            val dustMultiplierFormatted = if (jarType.fossilDustMultiplier == 1.0) "1.0x (Baseline)" else "${String.format("%.2f", jarType.fossilDustMultiplier)}x (+${((jarType.fossilDustMultiplier - 1.0) * 100).toInt()}%)"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FFB74D))
                    .border(0.8.dp, Color(0x66FFB74D), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fossil Dust Multiplier:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082)
                    )
                }
                Text(
                    text = dustMultiplierFormatted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = jarType.description,
                fontSize = 12.sp,
                color = Color(0xFFCFD8DC),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isUnlocked && state != null) {
                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33000000))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Population", fontSize = 10.sp, color = Color(0xFF90A4AE))
                        Text(
                            text = NumberFormatter.format(state.population),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF81D4FA)
                        )
                    }
                    Column {
                        Text(text = "Organic Matter", fontSize = 10.sp, color = Color(0xFF90A4AE))
                        Text(
                            text = NumberFormatter.format(state.organicMatter),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                    Column {
                        Text(text = "Age", fontSize = 10.sp, color = Color(0xFF90A4AE))
                        Text(
                            text = NumberFormatter.formatYears(state.totalInGameYears),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC80)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isActive) {
                    Button(
                        onClick = onSelect,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("select_jar_${jarType.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37474F),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Switch to this Jar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Locked Jar Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF90A4AE), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cost: ${NumberFormatter.format(jarType.unlockCostFD)} FD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }

                    Button(
                        onClick = onUnlock,
                        enabled = canAffordUnlock,
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("unlock_jar_${jarType.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB74D),
                            contentColor = Color(0xFF1E140A),
                            disabledContainerColor = Color(0x33455A64),
                            disabledContentColor = Color(0x66FFFFFF)
                        )
                    ) {
                        Text(text = "Unlock Jar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
