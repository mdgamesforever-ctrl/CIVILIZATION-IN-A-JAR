package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.ExtinctionType
import com.example.ui.ActiveExtinctionState
import com.example.util.NumberFormatter

@Composable
fun ExtinctionDialog(
    extinctionState: ActiveExtinctionState,
    onContinue: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("extinction_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (extinctionState.extinctionType == ExtinctionType.PEACEFUL_ASCENSION) {
                    Color(0xFF1E1035)
                } else {
                    Color(0xFF1F0D0D)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (extinctionState.isAnimating) {
                    // Animating Collapse Sequence
                    Text(
                        text = "EXTINCTION EVENT IN PROGRESS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = if (extinctionState.extinctionType == ExtinctionType.PEACEFUL_ASCENSION) Color(0xFFFFD700) else Color(0xFFFF5252),
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = extinctionState.extinctionType.displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = extinctionState.extinctionType.description,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFE0E0E0)
                    )
                } else {
                    // Final Summary Screen
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CIVILIZATION COLLAPSED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (extinctionState.extinctionType == ExtinctionType.PEACEFUL_ASCENSION) Color(0xFFFFD700) else Color(0xFFFF8A80),
                                letterSpacing = 1.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = extinctionState.extinctionType.displayName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fossil Dust Harvested Highlight Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF3E2723), Color(0xFF2E1A17))
                                        )
                                    )
                                    .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(18.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "FOSSIL DUST HARVESTED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFFCC80)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "💎", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "+${NumberFormatter.format(extinctionState.fossilDustEarned)}",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Stats Breakdown
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x33000000))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatRow(label = "Peak Population", value = NumberFormatter.format(extinctionState.peakPopulation))
                                StatRow(label = "Era Reached", value = extinctionState.eraReached.title)
                                StatRow(label = "Years Survived", value = NumberFormatter.formatYears(extinctionState.yearsSurvived))
                                if (extinctionState.greatResetBonusPercent > 0) {
                                    StatRow(
                                        label = "Great Reset Bonus",
                                        value = "+${(extinctionState.greatResetBonusPercent * 100).toInt()}% (Total: ${String.format("%.2f", extinctionState.newGreatResetMultiplier)}x)"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            // Rebirth / Continue Button
                            Button(
                                onClick = onContinue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("rebirth_continue_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFB74D),
                                    contentColor = Color(0xFF1E140A)
                                )
                            ) {
                                Text(
                                    text = "Rebirth Civilization (Era 1)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFFB0BEC5))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
