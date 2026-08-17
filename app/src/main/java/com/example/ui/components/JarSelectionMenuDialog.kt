package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.data.Era
import com.example.data.JarStateEntity
import com.example.data.JarType
import com.example.util.NumberFormatter

@Composable
fun JarSelectionMenuDialog(
    activeJarId: String,
    allJars: List<JarStateEntity>,
    fossilDust: Double,
    onSelectJar: (String) -> Unit,
    onUnlockJar: (JarType) -> Unit,
    onOpenKitchenCounter: () -> Unit,
    onDismiss: () -> Unit
) {
    val jarStatesMap = allJars.associateBy { it.jarId }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Color(0x66FFB74D), RoundedCornerShape(24.dp))
                .testTag("jar_selection_menu_dialog"),
            color = Color(0xFF16121D)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFB74D), Color(0xFFFF7043))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏺", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SELECT JAR BIOME",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Unique Fossil Dust Multipliers",
                                fontSize = 11.sp,
                                color = Color(0xFFFFB74D),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("close_jar_selection_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fossil Dust Bank Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFB74D))
                        .border(1.dp, Color(0x44FFB74D), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Available Fossil Dust:",
                            fontSize = 12.sp,
                            color = Color(0xFFCFD8DC)
                        )
                    }
                    Text(
                        text = "${NumberFormatter.format(fossilDust)} FD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Jar Menu Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(JarType.entries, key = { it.id }) { jarType ->
                        val state = jarStatesMap[jarType.id]
                        val isUnlocked = state?.isUnlocked == true
                        val isActive = jarType.id == activeJarId
                        val canAffordUnlock = fossilDust >= jarType.unlockCostFD

                        JarMenuItemCard(
                            jarType = jarType,
                            state = state,
                            isUnlocked = isUnlocked,
                            isActive = isActive,
                            canAffordUnlock = canAffordUnlock,
                            onSelect = { onSelectJar(jarType.id) },
                            onUnlock = { onUnlockJar(jarType) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Navigation to Deep Kitchen Counter Screen
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenKitchenCounter()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("open_kitchen_counter_from_menu"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55FFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCFD8DC))
                ) {
                    Text(
                        text = "🔎 View All 5 Worlds on Kitchen Counter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun JarMenuItemCard(
    jarType: JarType,
    state: JarStateEntity?,
    isUnlocked: Boolean,
    isActive: Boolean,
    canAffordUnlock: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val era = state?.let { Era.fromIndex(it.currentEraIndex) } ?: Era.PRIMORDIAL_SOUP
    val dustMultiplierFormatted = if (jarType.fossilDustMultiplier == 1.0) "1.0x (Baseline)" else "${String.format("%.2f", jarType.fossilDustMultiplier)}x (+${((jarType.fossilDustMultiplier - 1.0) * 100).toInt()}%)"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked && !isActive, onClick = onSelect)
            .testTag("jar_menu_item_${jarType.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF2B2035) else Color(0xFF1E1925)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 1.8.dp else 1.dp,
            color = if (isActive) Color(0xFFFFB74D) else if (isUnlocked) Color(0x33FFFFFF) else Color(0x15FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Item Row: Emoji, Name, Biome, and Status Badge
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(jarType.accentColorHex).copy(alpha = 0.2f))
                            .border(1.dp, Color(jarType.accentColorHex).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = jarType.emoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = jarType.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = jarType.biomeName,
                            fontSize = 11.sp,
                            color = Color(jarType.accentColorHex),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Active status indicator
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fossil Dust Bonus Multiplier Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FFB74D))
                    .border(0.8.dp, Color(0x66FFB74D), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Fossil Dust Multiplier:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082)
                    )
                }
                Text(
                    text = dustMultiplierFormatted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Biome Secondary Multipliers (Growth, Resources)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2200E5FF))
                        .padding(vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌱 ${jarType.growthMultiplier}x Growth",
                        fontSize = 10.sp,
                        color = Color(0xFF80D8FF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x22FFD54F))
                        .padding(vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧪 ${jarType.resourceMultiplier}x Yield",
                        fontSize = 10.sp,
                        color = Color(0xFFFFE082),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Unlocked State or Locked Action
            if (isUnlocked && state != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Era ${era.index}: ${era.title} (${NumberFormatter.format(state.population)} pop)",
                        fontSize = 10.sp,
                        color = Color(0xFF90A4AE)
                    )

                    if (!isActive) {
                        Button(
                            onClick = onSelect,
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("select_jar_btn_${jarType.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4527A0),
                                contentColor = Color.White
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text(text = "Switch Jar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Locked Jar Row with unlock button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cost: ${NumberFormatter.format(jarType.unlockCostFD)} FD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }

                    Button(
                        onClick = onUnlock,
                        enabled = canAffordUnlock,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("unlock_jar_btn_${jarType.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB74D),
                            contentColor = Color(0xFF1E140A),
                            disabledContainerColor = Color(0x33455A64),
                            disabledContentColor = Color(0x66FFFFFF)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(text = "Unlock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
