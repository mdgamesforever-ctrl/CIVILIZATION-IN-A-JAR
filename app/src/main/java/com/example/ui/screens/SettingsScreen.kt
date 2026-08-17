package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfileEntity

@Composable
fun SettingsScreen(
    profile: PlayerProfileEntity?,
    onUpdateSettings: (sound: Boolean, music: Boolean, shake: Boolean, notifs: Boolean) -> Unit,
    onResetGame: () -> Unit,
    onBackToJar: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    val soundEnabled = profile?.soundEnabled ?: true
    val musicEnabled = profile?.musicEnabled ?: true
    val shakeEnabled = profile?.shakeEnabled ?: true
    val notifsEnabled = profile?.notificationsEnabled ?: true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141018))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToJar,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SETTINGS & LORE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Audio & Controls Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A24)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AUDIO & CONTROLS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sound FX
                SettingToggleRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Sound Effects",
                    subtitle = "Tap pops, upgrade chimes, extinction alarms",
                    isChecked = soundEnabled,
                    testTag = "toggle_sound_effects",
                    onCheckedChange = { onUpdateSettings(it, musicEnabled, shakeEnabled, notifsEnabled) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ambient Music
                SettingToggleRow(
                    icon = Icons.Default.MusicNote,
                    title = "Era Ambient Tone Synthesizer",
                    subtitle = "Generative soothing ambient pad chords",
                    isChecked = musicEnabled,
                    testTag = "toggle_ambient_music",
                    onCheckedChange = { onUpdateSettings(soundEnabled, it, shakeEnabled, notifsEnabled) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accelerometer Shake
                SettingToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Physical Phone Shake (Earthquake)",
                    subtitle = "Shake phone physically to trigger cosmic tremors",
                    isChecked = shakeEnabled,
                    testTag = "toggle_shake_detector",
                    onCheckedChange = { onUpdateSettings(soundEnabled, musicEnabled, it, notifsEnabled) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Basement Lore & Story Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A24)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GRANDMA'S BASEMENT ARCHIVES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The Story So Far...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "While rummaging behind dusty preserves on the basement kitchen counter, you found an old mason jar. Inside, single-celled life suddenly began replicating. As generations pass, their history unfolds entirely behind the glass.\n\nExtinction is never the end — each collapse compacts millions of years into Fossil Dust, unlocking eternal primordial vigor for future worlds.",
                    fontSize = 12.sp,
                    color = Color(0xFFCFD8DC),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Data Danger Zone Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF221111)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FF5252))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DANGER ZONE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Erase all jar states, history records, and Fossil Dust permanently.",
                    fontSize = 12.sp,
                    color = Color(0xFFCFD8DC)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("reset_all_data_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Reset All Game Data", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Wipe All Progress?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you completely sure? This will delete all 5 jars, all Fossil Dust, and all history forever.", color = Color(0xFFCFD8DC)) },
            containerColor = Color(0xFF1E1A24),
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Yes, Delete Everything")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF81D4FA),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF90A4AE)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFB74D),
                checkedTrackColor = Color(0xFF4A3416),
                uncheckedThumbColor = Color(0xFF90A4AE),
                uncheckedTrackColor = Color(0xFF37474F)
            )
        )
    }
}
