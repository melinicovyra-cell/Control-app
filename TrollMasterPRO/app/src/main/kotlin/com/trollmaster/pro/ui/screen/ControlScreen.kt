package com.trollmaster.pro.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.trollmaster.pro.data.model.*
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    state: AppState,
    onCommand: (String, Map<String, String>) -> Unit,
    onCombo: (ComboCommand) -> Unit,
    onBack: () -> Unit
) {
    val user = state.selectedUser ?: return
    var selectedCategory by remember { mutableStateOf(CommandCategory.KILL) }
    var commandDialog by remember { mutableStateOf<Command?>(null) }
    var resultSnack by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.lastCommandResult) {
        if (state.lastCommandResult != null) {
            resultSnack = state.lastCommandResult
            kotlinx.coroutines.delay(2500)
            resultSnack = null
        }
    }

    val accentColor = when (selectedCategory) {
        CommandCategory.VIP -> OrangeAccent
        CommandCategory.ADMIN -> PurpleAccent
        CommandCategory.KILL -> RedAccent
        else -> GreenAccent
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🎯 ${user.name}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Game: ${user.game} • HP: ${user.hp}%",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GreenAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCard),
                actions = {
                    if (state.isAdminActive) {
                        Text("👑", fontSize = 18.sp, modifier = Modifier.padding(end = 12.dp))
                    } else if (state.isVipActive) {
                        Text("⭐", fontSize = 18.sp, modifier = Modifier.padding(end = 12.dp))
                    }
                }
            )
        },
        snackbarHost = {
            if (resultSnack != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (resultSnack!!.startsWith("✓")) GreenAccent.copy(alpha = 0.9f)
                            else if (resultSnack!!.startsWith("▶")) GreenAccent.copy(alpha = 0.7f)
                            else RedAccent.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            resultSnack!!,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = BgDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category tabs
            CategoryTabs(
                selected = selectedCategory,
                isVipActive = state.isVipActive,
                isAdminActive = state.isAdminActive,
                onSelect = { selectedCategory = it }
            )

            Divider(color = Divider, thickness = 1.dp)

            // Content
            when (selectedCategory) {
                CommandCategory.COMBO -> ComboPanel(
                    combos = COMBO_COMMANDS,
                    isRunning = state.isComboRunning,
                    accentColor = accentColor,
                    onCombo = onCombo
                )
                else -> CommandGrid(
                    commands = COMMANDS.filter { it.category == selectedCategory },
                    isVipActive = state.isVipActive,
                    isAdminActive = state.isAdminActive,
                    accentColor = accentColor,
                    onCommandClick = { cmd ->
                        if (cmd.params.isEmpty()) {
                            onCommand(cmd.id, emptyMap())
                        } else {
                            commandDialog = cmd
                        }
                    }
                )
            }
        }
    }

    // Command dialog for params
    commandDialog?.let { cmd ->
        CommandParamDialog(
            command = cmd,
            isVipActive = state.isVipActive,
            isAdminActive = state.isAdminActive,
            onDismiss = { commandDialog = null },
            onConfirm = { params ->
                commandDialog = null
                onCommand(cmd.id, params)
            },
            soundPresets = if (cmd.id == "sound" || cmd.id == "loopsound") SOUND_PRESETS else emptyList(),
            chatPresets = if (cmd.id == "chat" || cmd.id == "chatspam") CHAT_PRESETS else emptyList()
        )
    }
}

@Composable
fun CategoryTabs(
    selected: CommandCategory,
    isVipActive: Boolean,
    isAdminActive: Boolean,
    onSelect: (CommandCategory) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgCard)
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CommandCategory.values().forEach { cat ->
            val locked = (cat == CommandCategory.VIP && !isVipActive && !isAdminActive) ||
                    (cat == CommandCategory.ADMIN && !isAdminActive)
            val accentColor = when (cat) {
                CommandCategory.VIP -> OrangeAccent
                CommandCategory.ADMIN -> PurpleAccent
                CommandCategory.KILL -> RedAccent
                else -> GreenAccent
            }
            val isSelected = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent)
                    .border(1.dp, if (isSelected) accentColor else Divider, RoundedCornerShape(8.dp))
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${if (locked) "🔒" else cat.emoji} ${cat.label}",
                    color = if (isSelected) accentColor else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CommandGrid(
    commands: List<Command>,
    isVipActive: Boolean,
    isAdminActive: Boolean,
    accentColor: Color,
    onCommandClick: (Command) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(commands) { cmd ->
            val locked = (cmd.requiresAdmin && !isAdminActive) ||
                    (cmd.requiresVip && !isVipActive && !isAdminActive)
            CommandButton(
                command = cmd,
                locked = locked,
                accentColor = accentColor,
                onClick = { if (!locked) onCommandClick(cmd) }
            )
        }
    }
}

@Composable
fun CommandButton(
    command: Command,
    locked: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "btn")
    val borderAlpha by animateFloatAsState(if (pressed) 0.8f else 0.25f, label = "border")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                if (!locked) {
                    pressed = true
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (locked) BgCard.copy(alpha = 0.5f) else BgCard
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (locked) Divider.copy(alpha = 0.3f) else accentColor.copy(alpha = borderAlpha)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (locked) "🔒" else command.emoji,
                fontSize = 24.sp,
                color = if (locked) TextSecondary.copy(alpha = 0.4f) else Color.Unspecified
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = command.name,
                fontSize = 10.sp,
                color = if (locked) TextSecondary.copy(alpha = 0.4f) else TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
            if (command.params.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text("⚙", fontSize = 8.sp, color = accentColor.copy(alpha = 0.6f))
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }
}

@Composable
fun ComboPanel(
    combos: List<ComboCommand>,
    isRunning: Boolean,
    accentColor: Color,
    onCombo: (ComboCommand) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(combos) { combo ->
            ComboCard(combo = combo, isRunning = isRunning, onClick = { onCombo(combo) })
        }
    }
}

@Composable
fun ComboCard(combo: ComboCommand, isRunning: Boolean, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "cs")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(enabled = !isRunning) {
                pressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GreenAccent.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(combo.emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                combo.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = TextPrimary, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${combo.steps.size} шагов",
                fontSize = 10.sp, color = TextSecondary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                combo.steps.joinToString(" → ") { it.action },
                fontSize = 9.sp,
                color = GreenAccent.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }
}

@Composable
fun CommandParamDialog(
    command: Command,
    isVipActive: Boolean,
    isAdminActive: Boolean,
    soundPresets: List<Pair<String, String>>,
    chatPresets: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Map<String, String>) -> Unit
) {
    val paramValues = remember {
        mutableStateMapOf<String, String>().apply {
            command.params.forEach { p -> put(p.key, p.defaultValue) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BgSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GreenAccent.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "${command.emoji} ${command.name}",
                    color = GreenAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(16.dp))

                command.params.forEach { param ->
                    Text(param.hint, color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = paramValues[param.key] ?: "",
                        onValueChange = { paramValues[param.key] = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenAccent,
                            unfocusedBorderColor = Divider,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = GreenAccent
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Sound presets
                if (soundPresets.isNotEmpty()) {
                    Text("Пресеты звуков:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        soundPresets.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { (name, id) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GreenAccent.copy(alpha = 0.12f))
                                            .clickable { paramValues["id"] = id }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(name, fontSize = 10.sp, color = GreenAccent, textAlign = TextAlign.Center)
                                    }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Chat presets
                if (chatPresets.isNotEmpty()) {
                    Text("Пресеты сообщений:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        chatPresets.forEach { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GreenAccent.copy(alpha = 0.08f))
                                    .clickable { paramValues["message"] = msg }
                                    .padding(8.dp)
                            ) {
                                Text(msg, fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Divider)
                    ) {
                        Text("Отмена")
                    }
                    Button(
                        onClick = { onConfirm(paramValues.toMap()) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                    ) {
                        Text("▶ Запустить", color = BgDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
