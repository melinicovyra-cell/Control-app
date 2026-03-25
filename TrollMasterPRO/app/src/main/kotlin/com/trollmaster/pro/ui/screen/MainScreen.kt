package com.trollmaster.pro.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.data.model.UserInfo
import com.trollmaster.pro.data.model.UserStatus
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState

private enum class SortOption(val label: String, val emoji: String) {
    DEFAULT("Онлайн", "📊"),
    BY_NAME("A→Z", "🔤"),
    BY_HP("HP↓", "❤"),
    BY_STATUS("Статус", "●")
}

private val avatarPalette = listOf(
    Color(0xFF4D7CFE),
    Color(0xFF00C896),
    Color(0xFFFF5252),
    Color(0xFFFF9800),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4),
    Color(0xFFE91E8C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: AppState,
    onSelectUser: (UserInfo) -> Unit,
    onSettings: () -> Unit,
    onSearchChange: (String) -> Unit
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse = pulseAnim.animateFloat(
        initialValue = 0.4f, targetValue = 1f, label = "p",
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    val focusManager = LocalFocusManager.current
    var sortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    val filteredUsers = remember(state.users, state.searchQuery, sortOption) {
        val filtered = if (state.searchQuery.isBlank()) state.users
        else state.users.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        when (sortOption) {
            SortOption.BY_NAME -> filtered.sortedBy { it.name.lowercase() }
            SortOption.BY_HP -> filtered.sortedByDescending { it.hp }
            SortOption.BY_STATUS -> filtered.sortedBy { it.status.ordinal }
            SortOption.DEFAULT -> filtered
        }
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard)
                                .border(1.dp, BlueAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            KLogoShape(modifier = Modifier.size(26.dp))
                        }
                        Column {
                            Text(
                                "TrollMaster PRO",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text("v4.0", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                },
                actions = {
                    if (state.isAdminActive) {
                        Text(
                            "👑 ADMIN", color = PurpleAccent, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PurpleAccent.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    } else if (state.isVipActive) {
                        Text(
                            "⭐ VIP", color = OrangeAccent, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(OrangeAccent.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyCard,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Status bar with ripple dot and animated count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Ripple dot
                    Box(contentAlignment = Alignment.Center) {
                        if (state.error == null) {
                            Box(
                                modifier = Modifier
                                    .size((8f + 8f * (1f - pulse.value)).dp)
                                    .clip(CircleShape)
                                    .background(GreenAccent.copy(alpha = pulse.value * 0.35f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (state.error == null) GreenAccent
                                    else RedAccent
                                )
                        )
                    }
                    Text(
                        if (state.error == null) "LIVE • 2с" else "ОШИБКА",
                        color = if (state.error == null) GreenAccent else RedAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                // Animated user count
                AnimatedContent(
                    targetState = state.users.size,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        } else {
                            slideInVertically { it } + fadeIn() togetherWith
                                    slideOutVertically { -it } + fadeOut()
                        }
                    },
                    label = "count"
                ) { count ->
                    Text(
                        "$count онлайн",
                        color = if (count > 0) TextSecondary else RedAccent.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            // Error banner
            AnimatedVisibility(visible = state.error != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RedAccent.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠", fontSize = 14.sp)
                    Text(state.error ?: "", color = RedAccent, fontSize = 13.sp)
                }
            }

            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по имени...", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange(""); focusManager.clearFocus() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = BlueAccent,
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Sort chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SortOption.entries.forEach { option ->
                    val isSelected = sortOption == option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (isSelected) BlueAccent.copy(alpha = 0.15f) else BgCard)
                            .border(
                                1.dp,
                                if (isSelected) BlueAccent.copy(alpha = 0.5f) else Divider,
                                RoundedCornerShape(7.dp)
                            )
                            .clickable { sortOption = option }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "${option.emoji} ${option.label}",
                            fontSize = 11.sp,
                            color = if (isSelected) BlueAccent else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            if (filteredUsers.isEmpty() && state.error == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (state.searchQuery.isNotBlank()) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Никого не найдено", color = TextSecondary, fontSize = 14.sp)
                        } else {
                            LoadingDots()
                            Spacer(Modifier.height(12.dp))
                            Text("Поиск игроков...", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            if (state.searchQuery.isBlank()) "ИГРОКИ" else "РЕЗУЛЬТАТЫ (${filteredUsers.size})",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                    items(filteredUsers, key = { it.name }) { user ->
                        UserCard(user = user, onSelect = { onSelectUser(user) })
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: UserInfo, onSelect: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "card_scale")

    val statusColor = when (user.status) {
        UserStatus.LIVE -> GreenAccent
        UserStatus.AFK -> OrangeAccent
        UserStatus.OFFLINE -> TextSecondary
    }
    val statusText = when (user.status) {
        UserStatus.LIVE -> "● LIVE"
        UserStatus.AFK -> "○ AFK"
        UserStatus.OFFLINE -> "✕ OFF"
    }
    val isLive = user.status == UserStatus.LIVE

    // Colored initials avatar
    val avatarColor = avatarPalette[user.name.hashCode().let { if (it < 0) -it else it } % avatarPalette.size]
    val initials = user.name.trim()
        .split(Regex("\\s+"))
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { user.name.take(2).uppercase() }

    val hpColor = when {
        user.hp > 60 -> GreenAccent
        user.hp > 30 -> OrangeAccent
        else -> RedAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                pressed = true
                onSelect()
            },
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLive) GreenAccent.copy(alpha = 0.35f) else Divider
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isLive) Modifier.background(
                        Brush.horizontalGradient(
                            listOf(GreenAccent.copy(alpha = 0.05f), Color.Transparent)
                        )
                    ) else Modifier
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored initials avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(avatarColor.copy(alpha = if (isLive) 0.2f else 0.1f))
                        .border(
                            1.dp,
                            avatarColor.copy(alpha = if (isLive) 0.6f else 0.25f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = avatarColor
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "🎮 ${user.game}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    // HP bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Divider)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((user.hp / 100f).coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(hpColor, hpColor.copy(alpha = 0.7f))
                                    )
                                )
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "❤ ${user.hp}%",
                        color = hpColor.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onSelect,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLive) GreenAccent else BgSurface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Выбрать",
                            color = if (isLive) BgDark else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
