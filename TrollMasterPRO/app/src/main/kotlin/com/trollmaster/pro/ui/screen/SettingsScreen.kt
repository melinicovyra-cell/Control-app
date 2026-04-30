package com.trollmaster.pro.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: AppState,
    onBack: () -> Unit,
    onActivateVip: (String) -> Boolean,
    onActivateAdmin: (String) -> Boolean,
    onDeactivateVip: () -> Unit,
    onDeactivateAdmin: () -> Unit,
    onUpdateRelayUrl: (String) -> Boolean,
    onToggleVibration: (Boolean) -> Unit = {}
) {
    var vipInput by remember { mutableStateOf("") }
    var admInput by remember { mutableStateOf("") }
    var relayInput by remember { mutableStateOf(state.relayUrl) }
    var vipError by remember { mutableStateOf(false) }
    var admError by remember { mutableStateOf(false) }
    var vipSuccess by remember { mutableStateOf(false) }
    var admSuccess by remember { mutableStateOf(false) }
    var showVipKey by remember { mutableStateOf(false) }
    var showAdmKey by remember { mutableStateOf(false) }
    var relaySuccess by remember { mutableStateOf(false) }
    var relayError by remember { mutableStateOf(false) }

    // Easter egg: tap version 5 times
    var versionTaps by remember { mutableIntStateOf(0) }
    var devMode by remember { mutableStateOf(false) }
    val devScale by animateFloatAsState(if (devMode) 1f else 0.95f, tween(300), label = "dev")

    LaunchedEffect(vipSuccess) { if (vipSuccess) { kotlinx.coroutines.delay(2000); vipSuccess = false } }
    LaunchedEffect(admSuccess) { if (admSuccess) { kotlinx.coroutines.delay(2000); admSuccess = false } }
    LaunchedEffect(relaySuccess) { if (relaySuccess) { kotlinx.coroutines.delay(2000); relaySuccess = false } }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Text("⚙ Настройки", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BlueAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyCard)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // VIP Section
            SettingsCard(
                title = "👑 VIP Активация",
                accentColor = OrangeAccent
            ) {
                if (state.isVipActive) {
                    ActiveBadge("VIP АКТИВЕН", OrangeAccent)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onDeactivateVip(); vipInput = "" },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RedAccent.copy(alpha = 0.4f))
                    ) {
                        Text("Деактивировать VIP")
                    }
                } else {
                    Text("Введите VIP ключ для разблокировки VIP команд:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vipInput,
                        onValueChange = { vipInput = it; vipError = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("VIP ключ", color = TextSecondary) },
                        visualTransformation = if (showVipKey) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = vipError,
                        trailingIcon = {
                            TextButton(onClick = { showVipKey = !showVipKey }) {
                                Text(if (showVipKey) "👁" else "🙈", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeAccent,
                            unfocusedBorderColor = Divider,
                            errorBorderColor = RedAccent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = OrangeAccent
                        )
                    )
                    if (vipError) {
                        Text("✗ Неверный VIP ключ", color = RedAccent, fontSize = 12.sp)
                    }
                    AnimatedVisibility(vipSuccess) {
                        Text("✓ VIP активирован!", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val ok = onActivateVip(vipInput)
                            if (ok) { vipSuccess = true; vipInput = "" }
                            else vipError = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        enabled = vipInput.isNotBlank()
                    ) {
                        Text("Активировать VIP", color = BgDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ADMIN Section
            SettingsCard(
                title = "🔑 ADMIN Активация",
                accentColor = PurpleAccent
            ) {
                if (state.isAdminActive) {
                    ActiveBadge("ADMIN АКТИВЕН", PurpleAccent)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onDeactivateAdmin(); admInput = "" },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RedAccent.copy(alpha = 0.4f))
                    ) {
                        Text("Деактивировать ADMIN")
                    }
                } else {
                    Text("ADMIN включает все VIP функции. Требует отдельный ключ.", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = admInput,
                        onValueChange = { admInput = it; admError = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ADMIN ключ", color = TextSecondary) },
                        visualTransformation = if (showAdmKey) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = admError,
                        trailingIcon = {
                            TextButton(onClick = { showAdmKey = !showAdmKey }) {
                                Text(if (showAdmKey) "👁" else "🙈", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = Divider,
                            errorBorderColor = RedAccent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PurpleAccent
                        )
                    )
                    if (admError) {
                        Text("✗ Неверный ADMIN ключ", color = RedAccent, fontSize = 12.sp)
                    }
                    AnimatedVisibility(admSuccess) {
                        Text("✓ ADMIN активирован!", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val ok = onActivateAdmin(admInput)
                            if (ok) { admSuccess = true; admInput = "" }
                            else admError = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        enabled = admInput.isNotBlank()
                    ) {
                        Text("Активировать ADMIN", color = BgDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Relay URL Section
            SettingsCard(
                title = "🌐 Relay API",
                accentColor = GreenAccent
            ) {
                Text("URL для связи с Lua скриптом:", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = relayInput,
                    onValueChange = { relayInput = it; relaySuccess = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Relay URL", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenAccent,
                        unfocusedBorderColor = Divider,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = GreenAccent
                    )
                )
                AnimatedVisibility(relaySuccess) {
                    Text("✓ URL сохранён", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                AnimatedVisibility(relayError) {
                    Text("✗ URL должен начинаться с http:// или https://", color = RedAccent, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        relayError = false
                        val ok = onUpdateRelayUrl(relayInput)
                        if (ok) relaySuccess = true else relayError = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                    enabled = relayInput.isNotBlank()
                ) {
                    Text("Сохранить URL", color = BgDark, fontWeight = FontWeight.Bold)
                }
            }

            // Notifications / Vibration card
            SettingsCard(title = "📱 Уведомления", accentColor = BlueAccent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            "Вибрация",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Тактильный отклик на команды",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = state.vibrationEnabled,
                        onCheckedChange = onToggleVibration,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDark,
                            checkedTrackColor = BlueAccent,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = BgSurface,
                            uncheckedBorderColor = Divider
                        )
                    )
                }
            }

            // Command History card
            if (state.commandHistory.isNotEmpty()) {
                SettingsCard(title = "📜 История команд", accentColor = BlueAccent) {
                    state.commandHistory.takeLast(10).reversed().forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.action,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "→ ${entry.target}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                formatRelativeTime(entry.timestampMs),
                                color = TextSecondary.copy(alpha = 0.55f),
                                fontSize = 10.sp
                            )
                        }
                        if (index < minOf(9, state.commandHistory.size - 1)) {
                            HorizontalDivider(
                                color = Divider.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Info card
            SettingsCard(title = "ℹ Информация", accentColor = TextSecondary) {
                InfoRow("Версия", "4.0", onClick = {
                    versionTaps++
                    if (versionTaps >= 5) devMode = !devMode
                })
                InfoRow("Min SDK", "Android 8.0 (API 26)")
                InfoRow("Relay", "npoint.io JSON API")
                InfoRow("Auto-refresh", "2 секунды")
                InfoRow("Избранных команд", "${state.favoriteCommands.size}")
                InfoRow("История команд", "${state.commandHistory.size} / 50")
                InfoRow("Тапов на K", "${state.kTapCount}${if (state.kTapCount >= 100) " ⭐" else ""}")
                InfoRow("Недавние цели", "${state.recentTargets.size}")
            }

            // Dev mode easter egg card
            AnimatedVisibility(visible = devMode) {
                SettingsCard(
                    title = "🔓 DEV MODE",
                    accentColor = BlueAccent
                ) {
                    Box(
                        modifier = Modifier
                            .scale(devScale)
                            .alpha(if (devMode) 1f else 0f)
                    ) {
                        Column {
                            InfoRow("VIP Key hash", if (state.vipKey.isNotEmpty()) "✓ задан" else "—")
                            InfoRow("Admin Key hash", if (state.admKey.isNotEmpty()) "✓ задан" else "—")
                            InfoRow("Relay URL", state.relayUrl.take(25) + if (state.relayUrl.length > 25) "…" else "")
                            InfoRow("VIP Active", if (state.isVipActive) "✓ да" else "нет")
                            InfoRow("Admin Active", if (state.isAdminActive) "✓ да" else "нет")
                            InfoRow("Favorites", "${state.favoriteCommands.size} команд")
                            InfoRow("History entries", "${state.commandHistory.size}")
                            if (state.commandHistory.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text("Последнее действие:", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    "${state.commandHistory.last().action} → ${state.commandHistory.last().target}",
                                    color = BlueAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
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
fun SettingsCard(title: String, accentColor: androidx.compose.ui.graphics.Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            HorizontalDivider(color = accentColor.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 10.dp))
            content()
        }
    }
}

@Composable
fun ActiveBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("✓ $text", color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun InfoRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(
            value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = if (onClick != null) Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ) else Modifier
        )
    }
}

private fun formatRelativeTime(timestampMs: Long): String {
    val diff = System.currentTimeMillis() - timestampMs
    return when {
        diff < 60_000L -> "${diff / 1000}с"
        diff < 3_600_000L -> "${diff / 60_000}м"
        diff < 86_400_000L -> "${diff / 3_600_000}ч"
        else -> "${diff / 86_400_000}д"
    }
}
