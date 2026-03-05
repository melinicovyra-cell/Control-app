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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onSettings: () -> Unit
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse = pulseAnim.animateFloat(
        initialValue = 0.6f, targetValue = 1f, label = "p",
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("☠", fontSize = 22.sp, color = GreenAccent)
                        Column {
                            Text(
                                "TrollMaster PRO",
                                color = GreenAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text("v4.0", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                },
                actions = {
                    // VIP/ADMIN badges
                    if (state.isAdminActive) {
                        Text("👑ADMIN", color = PurpleAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp))
                    } else if (state.isVipActive) {
                        Text("⭐VIP", color = OrangeAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp))
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgCard,
                    titleContentColor = GreenAccent
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (state.error == null) GreenAccent.copy(alpha = pulse.value) else RedAccent)
                    )
                    Text(
                        if (state.error == null) "LIVE • Auto-refresh 2s" else "ERROR",
                        color = if (state.error == null) GreenAccent else RedAccent,
                        fontSize = 12.sp
                    )
                }
                Text(
                    "${state.users.size} жертв",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RedAccent.copy(alpha = 0.15f))
                        .padding(12.dp)
                ) {
                    Text("⚠ ${state.error}", color = RedAccent, fontSize = 13.sp)
                }
            }

            if (state.users.isEmpty() && state.error == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LoadingDots()
                        Spacer(Modifier.height(12.dp))
                        Text("Ищем жертв...", color = TextSecondary, fontSize = 14.sp)
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
                            "ЖЕРТВЫ ОНЛАЙН",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                    items(state.users) { user ->
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                pressed = true
                onSelect()
            },
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.status == UserStatus.LIVE) GreenAccent.copy(alpha = 0.3f) else Divider
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar / icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GreenAccent.copy(alpha = 0.12f))
                    .border(1.dp, GreenAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
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
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🎮 ${user.game}", color = TextSecondary, fontSize = 12.sp)
                    Text("❤️ ${user.hp}%", color = if (user.hp < 50) RedAccent else TextSecondary, fontSize = 12.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                // HP bar
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Divider)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((60 * user.hp / 100f).dp)
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
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = onSelect,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Выбрать", color = BgDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
