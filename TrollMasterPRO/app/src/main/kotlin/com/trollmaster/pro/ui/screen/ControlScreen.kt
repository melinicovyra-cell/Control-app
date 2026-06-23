package com.trollmaster.pro.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.trollmaster.pro.data.model.*
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState
import com.trollmaster.pro.util.VibrationHelper

private val controlAvatarPalette = listOf(
    Color(0xFF4D7CFE), Color(0xFF00C896), Color(0xFFFF5252),
    Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFFE91E8C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    state: AppState,
    onCommand: (String, Map<String, String>) -> Unit,
    onCombo: (ComboCommand) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
    val user = state.selectedUser ?: return
    var selectedCategory by remember { mutableStateOf(CommandCategory.KILL) }
    var commandDialog by remember { mutableStateOf<Command?>(null) }
    var resultSnack by remember { mutableStateOf<String?>(null) }
    var commandSearch by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val ctx = LocalContext.current
    var killStreak by remember { mutableIntStateOf(0) }
    var streakMsg by remember { mutableStateOf("") }

    val avatarColor = controlAvatarPalette[user.name.hashCode().let { if (it < 0) -it else it } % controlAvatarPalette.size]
    val initials = user.name.trim().split(Regex("\\s+")).take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        .ifEmpty { user.name.take(2).uppercase() }
    val hpFraction by animateFloatAsState(
        targetValue = (user.hp / 100f).coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "hp_top"
    )
    val hpColor = when {
        user.hp > 60 -> GreenAccent
        user.hp > 30 -> OrangeAccent
        else -> RedAccent
    }
    val statusColor = when (user.status) {
        UserStatus.LIVE -> GreenAccent
        UserStatus.AFK -> OrangeAccent
        UserStatus.OFFLINE -> TextSecondary
    }

    LaunchedEffect(state.lastCommandResult) {
        if (state.lastCommandResult != null) {
            resultSnack = state.lastCommandResult
            when {
                state.lastCommandResult!!.startsWith("✓") -> {
                    if (state.vibrationEnabled) VibrationHelper.success(ctx)
                    killStreak++
                    streakMsg = when (killStreak) {
                        3 -> "🔥 ТРИПЛ x3!"
                        5 -> "💀 ПЕНТА x5!"
                        7 -> "⚡ ЛЕГЕНДА x7!"
                        10 -> "👑 УЛЬТРА x10!"
                        else -> ""
                    }
                }
                state.lastCommandResult!!.startsWith("✗") -> {
                    if (state.vibrationEnabled) VibrationHelper.error(ctx)
                    killStreak = 0
                    streakMsg = ""
                }
            }
            kotlinx.coroutines.delay(2500)
            resultSnack = null
            onClearResult()
            if (streakMsg.isNotEmpty()) {
                kotlinx.coroutines.delay(600)
                streakMsg = ""
            }
        }
    }

    val accentColor = when (selectedCategory) {
        CommandCategory.VIP -> OrangeAccent
        CommandCategory.ADMIN -> PurpleAccent
        CommandCategory.KILL -> RedAccent
        CommandCategory.FAVORITES -> BlueAccent
        else -> GreenAccent
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(avatarColor.copy(alpha = 0.18f))
                                .border(2.dp, statusColor.copy(alpha = 0.8f), RoundedCornerShape(11.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 14.sp, fontWeight = FontWeight.Black, color = avatarColor)
                        }
                        Column {
                            Text(
                                user.name, color = TextPrimary, fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(52.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Divider)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(hpFraction)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Brush.horizontalGradient(listOf(hpColor, hpColor.copy(alpha = 0.6f))))
                                    )
                                }
                                Text("${user.hp}%", color = hpColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("·", color = TextSecondary.copy(alpha = 0.5f), fontSize = 10.sp)
                                Text(
                                    user.game, color = TextSecondary, fontSize = 10.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 90.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BlueAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyCard),
                actions = {
                    if (killStreak >= 3) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF6B00).copy(alpha = 0.2f), Color(0xFFFF0060).copy(alpha = 0.2f))
                                    )
                                )
                                .border(1.dp, Color(0xFFFF6B00).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🔥 $killStreak", color = Color(0xFFFF8C00), fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    when {
                        state.isAdminActive -> Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PurpleAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) { Text("👑 ADM", color = PurpleAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        state.isVipActive -> Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(OrangeAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) { Text("⭐ VIP", color = OrangeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            )
        },
        snackbarHost = {
            if (resultSnack != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val snackBrush = when {
                        resultSnack!!.startsWith("✓") -> Brush.horizontalGradient(
                            listOf(GreenAccent.copy(alpha = 0.92f), Color(0xFF009944).copy(alpha = 0.88f))
                        )
                        resultSnack!!.startsWith("▶") -> Brush.horizontalGradient(
                            listOf(BlueAccent.copy(alpha = 0.92f), Color(0xFF2244FF).copy(alpha = 0.88f))
                        )
                        else -> Brush.horizontalGradient(
                            listOf(RedAccent.copy(alpha = 0.92f), Color(0xFFCC0033).copy(alpha = 0.88f))
                        )
                    }
                    val snackBorder = when {
                        resultSnack!!.startsWith("✓") -> GreenAccent.copy(alpha = 0.5f)
                        resultSnack!!.startsWith("▶") -> BlueAccent.copy(alpha = 0.5f)
                        else -> RedAccent.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, snackBorder, RoundedCornerShape(14.dp))
                            .background(snackBrush)
                            .padding(horizontal = 20.dp, vertical = 13.dp)
                    ) {
                        Text(resultSnack!!, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            if (state.isComboRunning) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = BlueAccent,
                    trackColor = NavyCard
                )
            }

            AnimatedVisibility(
                visible = streakMsg.isNotEmpty(),
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFFF6B00), Color(0xFFFF0080)))
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(streakMsg, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
                }
            }

            CategoryTabs(
                selected = selectedCategory,
                isVipActive = state.isVipActive,
                isAdminActive = state.isAdminActive,
                favCount = state.favoriteCommands.size,
                onSelect = {
                    selectedCategory = it
                    commandSearch = ""
                    focusManager.clearFocus()
                }
            )

            if (selectedCategory != CommandCategory.COMBO) {
                OutlinedTextField(
                    value = commandSearch,
                    onValueChange = { commandSearch = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    placeholder = { Text("Поиск команды...", color = TextSecondary, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        AnimatedVisibility(visible = commandSearch.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            IconButton(onClick = { commandSearch = ""; focusManager.clearFocus() }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor.copy(alpha = 0.6f),
                        unfocusedBorderColor = Divider.copy(alpha = 0.5f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = accentColor,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )
            }

            when (selectedCategory) {
                CommandCategory.COMBO -> ComboPanel(
                    combos = COMBO_COMMANDS,
                    isRunning = state.isComboRunning,
                    accentColor = accentColor,
                    onCombo = onCombo
                )
                CommandCategory.FAVORITES -> {
                    val favCmds = COMMANDS
                        .filter { it.id in state.favoriteCommands }
                        .filter { commandSearch.isBlank() || it.name.contains(commandSearch, ignoreCase = true) }
                    if (state.favoriteCommands.isEmpty()) {
                        ControlEmptyState("⭐", "Нет избранных команд", "Нажмите ★ на команде чтобы добавить")
                    } else if (favCmds.isEmpty()) {
                        ControlEmptyState("🔍", "Не найдено", "")
                    } else {
                        CommandGrid(
                            commands = favCmds,
                            isVipActive = state.isVipActive,
                            isAdminActive = state.isAdminActive,
                            accentColor = accentColor,
                            favoriteIds = state.favoriteCommands,
                            onCommandClick = { cmd ->
                                if (cmd.params.isEmpty()) onCommand(cmd.id, emptyMap())
                                else commandDialog = cmd
                            },
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
                else -> {
                    val cmds = COMMANDS
                        .filter { it.category == selectedCategory }
                        .filter { commandSearch.isBlank() || it.name.contains(commandSearch, ignoreCase = true) }
                    if (cmds.isEmpty()) {
                        ControlEmptyState("🔍", "Не найдено", "")
                    } else {
                        CommandGrid(
                            commands = cmds,
                            isVipActive = state.isVipActive,
                            isAdminActive = state.isAdminActive,
                            accentColor = accentColor,
                            favoriteIds = state.favoriteCommands,
                            onCommandClick = { cmd ->
                                if (cmd.params.isEmpty()) onCommand(cmd.id, emptyMap())
                                else commandDialog = cmd
                            },
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }

    commandDialog?.let { cmd ->
        CommandParamDialog(
            command = cmd,
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
private fun ControlEmptyState(emoji: String, title: String, subtitle: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(title, color = TextSecondary, fontSize = 14.sp)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = TextSecondary.copy(alpha = 0.6f), fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selected: CommandCategory,
    isVipActive: Boolean,
    isAdminActive: Boolean,
    favCount: Int,
    onSelect: (CommandCategory) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().background(BgSurface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommandCategory.values().forEach { cat ->
                val locked = (cat == CommandCategory.VIP && !isVipActive && !isAdminActive) ||
                        (cat == CommandCategory.ADMIN && !isAdminActive)
                val tabColor = when (cat) {
                    CommandCategory.VIP -> OrangeAccent
                    CommandCategory.ADMIN -> PurpleAccent
                    CommandCategory.KILL -> RedAccent
                    CommandCategory.FAVORITES -> BlueAccent
                    else -> GreenAccent
                }
                val isSelected = cat == selected
                val label = if (cat == CommandCategory.FAVORITES && favCount > 0)
                    "${cat.emoji} ${cat.label} ($favCount)"
                else
                    "${if (locked) "🔒" else cat.emoji} ${cat.label}"

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected)
                                Brush.horizontalGradient(listOf(tabColor.copy(alpha = 0.28f), tabColor.copy(alpha = 0.13f)))
                            else
                                Brush.horizontalGradient(listOf(BgCard, BgCard))
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) tabColor.copy(alpha = 0.75f) else Divider.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelect(cat) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) tabColor else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
    HorizontalDivider(color = Divider, thickness = 1.dp)
}

@Composable
fun CommandGrid(
    commands: List<Command>,
    isVipActive: Boolean,
    isAdminActive: Boolean,
    accentColor: Color,
    favoriteIds: Set<String>,
    onCommandClick: (Command) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(commands) { cmd ->
            val locked = (cmd.requiresAdmin && !isAdminActive) ||
                    (cmd.requiresVip && !isVipActive && !isAdminActive)
            CommandButton(
                command = cmd,
                locked = locked,
                accentColor = accentColor,
                isFavorite = cmd.id in favoriteIds,
                onToggleFavorite = { onToggleFavorite(cmd.id) },
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
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "btn")

    val borderColor = when {
        locked -> Divider.copy(alpha = 0.2f)
        isFavorite -> BlueAccent.copy(alpha = 0.65f)
        else -> accentColor.copy(alpha = 0.35f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .clickable {
                if (!locked) {
                    pressed = true
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (locked) BgCard else accentColor.copy(alpha = 0.09f),
                            BgCard
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (locked) "🔒" else command.emoji,
                    fontSize = 30.sp,
                    color = if (locked) TextSecondary.copy(alpha = 0.3f) else Color.Unspecified
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = command.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (locked) TextSecondary.copy(alpha = 0.35f) else TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                if (command.params.isNotEmpty() && !locked) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text("⚙ параметры", fontSize = 8.sp, color = accentColor)
                    }
                }
            }
            if (!locked) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (isFavorite) BlueAccent else TextSecondary.copy(alpha = 0.3f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(17.dp)
                        .clickable { onToggleFavorite() }
                )
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
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRunning) BlueAccent.copy(alpha = 0.7f) else BlueAccent.copy(alpha = 0.25f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(BlueAccent.copy(alpha = 0.1f), BgCard)
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(combo.emoji, fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    combo.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BlueAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${combo.steps.size} шагов", fontSize = 10.sp, color = BlueAccent, fontWeight = FontWeight.Bold)
                }
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
                if (isRunning) {
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(2.dp)),
                        color = BlueAccent,
                        trackColor = Divider
                    )
                }
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
fun CommandParamDialog(
    command: Command,
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
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BlueAccent.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "${command.emoji} ${command.name}",
                    color = TextPrimary,
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
                            focusedBorderColor = BlueAccent,
                            unfocusedBorderColor = Divider,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = BlueAccent
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                }

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
                                            .background(BlueAccent.copy(alpha = 0.1f))
                                            .border(1.dp, BlueAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .clickable { paramValues["id"] = id }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(name, fontSize = 10.sp, color = BlueAccent, textAlign = TextAlign.Center)
                                    }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                if (chatPresets.isNotEmpty()) {
                    Text("Пресеты сообщений:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        chatPresets.forEach { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BgSurface)
                                    .border(1.dp, Divider, RoundedCornerShape(6.dp))
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
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                    ) {
                        Text("▶ Запустить", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
