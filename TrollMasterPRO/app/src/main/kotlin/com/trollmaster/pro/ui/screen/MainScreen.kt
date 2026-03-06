package com.trollmaster.pro.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.trollmaster.pro.ui.theme.*
import com.trollmaster.pro.ui.viewmodel.AppState

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
        initialValue = 0.5f, targetValue = 1f, label = "p",
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse)
    )
    val focusManager = LocalFocusManager.current

    val filteredUsers = if (state.searchQuery.isBlank()) state.users
    else state.users.filter { it.name.contains(state.searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // K logo mini
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard),
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
            // Status bar
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
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (state.error == null) GreenAccent.copy(alpha = pulse.value)
                                else RedAccent
                            )
                    )
                    Text(
                        if (state.error == null) "LIVE • обновление 2с" else "ОШИБКА",
                        color = if (state.error == null) GreenAccent else RedAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "${state.users.size} онлайн",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
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
                            if (state.searchQuery.isBlank()) "ОНЛАЙН" else "РЕЗУЛЬТАТЫ (${filteredUsers.size})",
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
        UserStatus.OFFLINE -> "✕ OFFLINE"
    }
    val isLive = user.status == UserStatus.LIVE

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
                            listOf(GreenAccent.copy(alpha = 0.04f), Color.Transparent)
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
                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isLive) GreenAccent.copy(alpha = 0.1f)
                            else BgSurface
                        )
                        .border(
                            1.dp,
                            if (isLive) GreenAccent.copy(alpha = 0.4f) else Divider,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 22.sp)
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🎮 ${user.game}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    // HP bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Divider)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(user.hp / 100f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        user.hp > 60 -> GreenAccent
                                        user.hp > 30 -> OrangeAccent
                                        else -> RedAccent
                                    }
                                )
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "❤ ${user.hp}%",
                        color = when {
                            user.hp > 60 -> GreenAccent.copy(alpha = 0.8f)
                            user.hp > 30 -> OrangeAccent
                            else -> RedAccent
                        },
                        fontSize = 11.sp
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
