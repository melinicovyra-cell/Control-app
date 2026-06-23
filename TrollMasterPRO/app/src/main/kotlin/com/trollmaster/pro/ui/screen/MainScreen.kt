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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.data.model.UserInfo
import com.trollmaster.pro.data.model.UserStatus
import com.trollmaster.pro.ui.component.SkeletonList
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState

private enum class SortOption(val label: String, val emoji: String) {
    DEFAULT("Онлайн", "📊"),
    BY_NAME("A→Z", "🔤"),
    BY_HP("HP↓", "❤"),
    BY_STATUS("Статус", "●")
}

private val avatarPalette = listOf(
    Color(0xFF4D7CFE), Color(0xFF00C896), Color(0xFFFF5252),
    Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFFE91E8C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: AppState,
    onSelectUser: (UserInfo) -> Unit,
    onSettings: () -> Unit,
    onSearchChange: (String) -> Unit
) {
    val avgHp = remember(state.users) {
        val live = state.users.filter { it.hp > 0 }
        if (live.isEmpty()) 0 else live.sumOf { it.hp } / live.size
    }
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse = pulseAnim.animateFloat(
        0.4f, 1f, label = "p",
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    val focusManager = LocalFocusManager.current
    var sortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    val filteredUsers = remember(state.users, state.searchQuery, sortOption) {
        val base = if (state.searchQuery.isBlank()) state.users
        else state.users.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        when (sortOption) {
            SortOption.BY_NAME -> base.sortedBy { it.name.lowercase() }
            SortOption.BY_HP -> base.sortedByDescending { it.hp }
            SortOption.BY_STATUS -> base.sortedBy { it.status.ordinal }
            SortOption.DEFAULT -> base
        }
    }

    val liveCount = state.users.count { it.status == UserStatus.LIVE }
    val afkCount = state.users.count { it.status == UserStatus.AFK }
    val offCount = state.users.count { it.status == UserStatus.OFFLINE }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard)
                                .border(1.dp, BlueAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            KLogoShape(Modifier.size(26.dp))
                        }
                        Column {
                            Text("TrollMaster PRO", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("v4.0", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                },
                actions = {
                    when {
                        state.isAdminActive -> {
                            Text(
                                "👑 ADMIN", color = PurpleAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(PurpleAccent.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        state.isVipActive -> {
                            Text(
                                "⭐ VIP", color = OrangeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(OrangeAccent.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyCard, titleContentColor = TextPrimary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Status bar with ripple dot + animated count ──────────
            Row(
                modifier = Modifier.fillMaxWidth().background(BgSurface).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (state.error == null) {
                            Box(
                                modifier = Modifier
                                    .size((8f + 9f * (1f - pulse.value)).dp)
                                    .clip(CircleShape)
                                    .background(GreenAccent.copy(alpha = pulse.value * 0.3f))
                            )
                        }
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape)
                                .background(if (state.error == null) GreenAccent else RedAccent)
                        )
                    }
                    Text(
                        if (state.error == null) "LIVE • 2с" else "ОШИБКА",
                        color = if (state.error == null) GreenAccent else RedAccent,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
                }
                AnimatedContent(
                    targetState = state.users.size,
                    transitionSpec = {
                        if (targetState > initialState)
                            slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                        else
                            slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                    }, label = "cnt"
                ) { count ->
                    Text("$count онлайн", color = TextSecondary, fontSize = 12.sp)
                }
            }

            // ── Error banner ─────────────────────────────────────────
            AnimatedVisibility(visible = state.error != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(RedAccent.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠", fontSize = 14.sp)
                    Text(state.error ?: "", color = RedAccent, fontSize = 13.sp)
                }
            }

            // ── Hero stats card ──────────────────────────────────────
            if (state.users.isNotEmpty()) {
                HeroStatsCard(
                    total = state.users.size,
                    live = liveCount,
                    avgHp = avgHp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip(liveCount, "LIVE", GreenAccent)
                    StatChip(afkCount, "AFK", OrangeAccent)
                    StatChip(offCount, "OFFLINE", TextSecondary)
                }
            }

            // ── Recent targets row ───────────────────────────────────
            if (state.recentTargets.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(
                        "НЕДАВНИЕ ЦЕЛИ",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        state.recentTargets.forEach { name ->
                            RecentTargetChip(
                                name = name,
                                isOnline = state.users.any { it.name == name },
                                onClick = {
                                    state.users.firstOrNull { it.name == name }?.let(onSelectUser)
                                }
                            )
                        }
                    }
                }
            }

            // ── Search bar ───────────────────────────────────────────
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                placeholder = { Text("Поиск по имени...", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange(""); focusManager.clearFocus() }) {
                            Icon(Icons.Default.Clear, null, tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent, unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    cursorColor = BlueAccent, focusedContainerColor = BgCard, unfocusedContainerColor = BgCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // ── Sort chips ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SortOption.entries.forEach { opt ->
                    val sel = sortOption == opt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (sel) BlueAccent.copy(alpha = 0.16f) else BgCard)
                            .border(1.dp, if (sel) BlueAccent.copy(alpha = 0.55f) else Divider, RoundedCornerShape(7.dp))
                            .clickable { sortOption = opt }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "${opt.emoji} ${opt.label}", fontSize = 11.sp,
                            color = if (sel) BlueAccent else TextSecondary,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── User list ────────────────────────────────────────────
            if (filteredUsers.isEmpty() && state.error == null) {
                if (state.searchQuery.isNotBlank()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Никого не найдено", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                } else {
                    SkeletonList(
                        count = 4,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            if (state.searchQuery.isBlank()) "ИГРОКИ" else "РЕЗУЛЬТАТЫ (${filteredUsers.size})",
                            color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
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

// ── Hero stats card ──────────────────────────────────────────────────────────

@Composable
private fun HeroStatsCard(total: Int, live: Int, avgHp: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BlueAccent.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            NavyCard,
                            BlueAccent.copy(alpha = 0.12f),
                            NavyCard
                        )
                    )
                )
                .padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroStatItem(value = total.toString(), label = "ВСЕГО", color = BlueAccent, modifier = Modifier.weight(1f))
                HeroDivider()
                HeroStatItem(value = live.toString(), label = "LIVE", color = GreenAccent, modifier = Modifier.weight(1f))
                HeroDivider()
                HeroStatItem(value = "$avgHp%", label = "СРЕД HP", color = OrangeAccent, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeroStatItem(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color = color,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun HeroDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Divider.copy(alpha = 0.6f))
    )
}

// ── Recent target chip ───────────────────────────────────────────────────────

@Composable
private fun RecentTargetChip(name: String, isOnline: Boolean, onClick: () -> Unit) {
    val initials = name.trim().split(Regex("\\s+")).take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        .ifEmpty { name.take(2).uppercase() }
    val color = avatarPalette[name.hashCode().let { if (it < 0) -it else it } % avatarPalette.size]

    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isOnline) color.copy(alpha = 0.15f) else BgCard)
            .border(
                1.dp,
                if (isOnline) color.copy(alpha = 0.5f) else Divider,
                RoundedCornerShape(18.dp)
            )
            .clickable(enabled = isOnline) { onClick() }
            .padding(start = 4.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = if (isOnline) 0.3f else 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        }
        Text(
            name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOnline) TextPrimary else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 100.dp)
        )
        if (!isOnline) {
            Text("○", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// ── Stat chip ────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(count: Int, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.28f), RoundedCornerShape(6.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text("$count $label", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ── User card ────────────────────────────────────────────────────────────────

@Composable
fun UserCard(user: UserInfo, onSelect: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "cs")

    val isLive = user.status == UserStatus.LIVE
    val isDanger = user.hp in 1..19

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
    val hpColor = when {
        user.hp > 60 -> GreenAccent
        user.hp > 30 -> OrangeAccent
        else -> RedAccent
    }
    val avatarColor = avatarPalette[user.name.hashCode().let { if (it < 0) -it else it } % avatarPalette.size]
    val initials = user.name.trim().split(Regex("\\s+")).take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        .ifEmpty { user.name.take(2).uppercase() }

    val animHp by animateFloatAsState(
        targetValue = (user.hp / 100f).coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "hp"
    )

    val dangerPulse = rememberInfiniteTransition(label = "dp")
    val dangerAlpha = dangerPulse.animateFloat(
        0.4f, 1f, label = "da",
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxWidth().scale(cardScale)) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { pressed = true; onSelect() },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isLive || isDanger) 1.5.dp else 1.dp,
                color = when {
                    isDanger && isLive -> RedAccent.copy(alpha = dangerAlpha.value * 0.8f)
                    isLive -> GreenAccent.copy(alpha = 0.45f)
                    else -> Divider
                }
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when {
                            isDanger && isLive -> Brush.horizontalGradient(
                                listOf(RedAccent.copy(alpha = 0.05f), BgCard, BgCard)
                            )
                            isLive -> Brush.horizontalGradient(
                                listOf(GreenAccent.copy(alpha = 0.06f), BgCard, BgCard)
                            )
                            else -> Brush.horizontalGradient(listOf(BgCard, BgCard))
                        }
                    )
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(
                                Brush.verticalGradient(
                                    listOf(statusColor, statusColor.copy(alpha = 0.15f))
                                )
                            )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(avatarColor.copy(alpha = if (isLive) 0.22f else 0.1f))
                                .border(
                                    width = if (isLive) 1.5.dp else 1.dp,
                                    color = avatarColor.copy(alpha = if (isLive) 0.65f else 0.22f),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 19.sp, fontWeight = FontWeight.Black, color = avatarColor)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    user.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (isDanger && isLive) {
                                    Text(
                                        "⚠ CRITI",
                                        fontSize = 9.sp, fontWeight = FontWeight.Black,
                                        color = RedAccent,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(RedAccent.copy(alpha = dangerAlpha.value * 0.18f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text("🎮 ${user.game}", color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth().height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)).background(Divider)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxHeight().fillMaxWidth(animHp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Brush.horizontalGradient(listOf(hpColor, hpColor.copy(alpha = 0.55f))))
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Text("❤ ${user.hp}%", color = hpColor.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isLive)
                                            Brush.horizontalGradient(listOf(GreenAccent, Color(0xFF00AA44)))
                                        else
                                            Brush.horizontalGradient(listOf(BgSurface, BgSurface))
                                    )
                                    .clickable { onSelect() }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
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
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }
}
