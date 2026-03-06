package com.trollmaster.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.trollmaster.pro.ui.screen.ControlScreen
import com.trollmaster.pro.ui.screen.MainScreen
import com.trollmaster.pro.ui.screen.SettingsScreen
import com.trollmaster.pro.ui.screen.SplashScreen
import com.trollmaster.pro.ui.theme.TrollMasterTheme
import com.trollmaster.pro.ui.viewmodel.MainViewModel

enum class Screen { SPLASH, MAIN, CONTROL, SETTINGS }

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TrollMasterTheme {
                val state by viewModel.state.collectAsState()
                var screen by remember { mutableStateOf(Screen.SPLASH) }

                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        when {
                            targetState == Screen.SPLASH ->
                                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                            targetState == Screen.CONTROL ->
                                slideInHorizontally(tween(300)) { it } togetherWith
                                        slideOutHorizontally(tween(300)) { -it }
                            initialState == Screen.CONTROL ->
                                slideInHorizontally(tween(300)) { -it } togetherWith
                                        slideOutHorizontally(tween(300)) { it }
                            targetState == Screen.SETTINGS ->
                                slideInVertically(tween(300)) { it } togetherWith
                                        slideOutVertically(tween(300)) { -it }
                            initialState == Screen.SETTINGS ->
                                slideInVertically(tween(300)) { -it } togetherWith
                                        slideOutVertically(tween(300)) { it }
                            else ->
                                fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                        }
                    },
                    label = "screen_transition"
                ) { currentScreen ->
                    when (currentScreen) {
                        Screen.SPLASH -> SplashScreen(
                            onFinished = { screen = Screen.MAIN }
                        )
                        Screen.MAIN -> MainScreen(
                            state = state,
                            onSelectUser = { user ->
                                viewModel.selectUser(user)
                                screen = Screen.CONTROL
                            },
                            onSettings = { screen = Screen.SETTINGS },
                            onSearchChange = { viewModel.setSearchQuery(it) }
                        )
                        Screen.CONTROL -> ControlScreen(
                            state = state,
                            onCommand = { action, params -> viewModel.sendCommand(action, params) },
                            onCombo = { combo -> viewModel.runCombo(combo) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onClearResult = { viewModel.clearResult() },
                            onBack = {
                                viewModel.clearSelection()
                                screen = Screen.MAIN
                            }
                        )
                        Screen.SETTINGS -> SettingsScreen(
                            state = state,
                            onBack = { screen = Screen.MAIN },
                            onActivateVip = { key -> viewModel.activateVip(key) },
                            onActivateAdmin = { key -> viewModel.activateAdmin(key) },
                            onDeactivateVip = { viewModel.deactivateVip() },
                            onDeactivateAdmin = { viewModel.deactivateAdmin() },
                            onUpdateRelayUrl = { url -> viewModel.updateRelayUrl(url) }
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopAutoRefresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startAutoRefresh()
    }
}
