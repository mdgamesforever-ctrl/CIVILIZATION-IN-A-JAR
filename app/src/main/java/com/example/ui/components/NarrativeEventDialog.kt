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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import com.example.data.NarrativeChoice
import com.example.data.NarrativeEvent

@Composable
fun NarrativeEventDialog(
    event: NarrativeEvent,
    onChoiceSelected: (NarrativeChoice) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("narrative_event_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                        letterSpacing = 1.2.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("event_dismiss_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFB0BEC5)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Event Icon / Emoji Thumbnail
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF37474F), Color(0xFF212121))
                            )
                        )
                        .border(1.dp, Color(0x33FFB74D), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.emoji, fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFFCFD8DC)
                )

                if (!event.dialogueText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF26232E))
                            .border(1.dp, Color(0x33BB86FC), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            if (!event.dialogueSpeaker.isNullOrBlank()) {
                                Text(
                                    text = "💬 ${event.dialogueSpeaker}:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBB86FC)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = event.dialogueText,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 18.sp,
                                color = Color(0xFFE1BEE7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Choice A Button
                ChoiceButton(
                    choice = event.choiceA,
                    buttonTag = "choice_a_button",
                    onClick = { onChoiceSelected(event.choiceA) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Choice B Button
                ChoiceButton(
                    choice = event.choiceB,
                    buttonTag = "choice_b_button",
                    onClick = { onChoiceSelected(event.choiceB) }
                )
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    choice: NarrativeChoice,
    buttonTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF263238), Color(0xFF1E272C))
                )
            )
            .border(1.dp, Color(0xFF455A64), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(buttonTag)
    ) {
        Column {
            Text(
                text = choice.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = choice.description,
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5)
            )
        }
    }
}
