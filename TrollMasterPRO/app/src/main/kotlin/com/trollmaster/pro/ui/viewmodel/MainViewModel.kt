package com.trollmaster.pro.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trollmaster.pro.data.RelayRepository
import com.trollmaster.pro.data.model.ComboCommand
import com.trollmaster.pro.data.model.UserInfo
import com.trollmaster.pro.util.CryptoUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryEntry(
    val action: String,
    val target: String,
    val timestampMs: Long = System.currentTimeMillis()
)

data class AppState(
    val users: List<UserInfo> = emptyList(),
    val selectedUser: UserInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastCommandResult: String? = null,
    val isVipActive: Boolean = false,
    val isAdminActive: Boolean = false,
    val vipKey: String = "",
    val admKey: String = "",
    val relayUrl: String = DEFAULT_RELAY_URL,
    val isComboRunning: Boolean = false,
    val favoriteCommands: Set<String> = emptySet(),
    val commandHistory: List<HistoryEntry> = emptyList(),
    val searchQuery: String = ""
) {
    companion object {
        const val DEFAULT_RELAY_URL = "https://api.npoint.io/d5decbe9a46d769f5419"
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("troll_prefs", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var repository: RelayRepository
    private var refreshJob: Job? = null

    init {
        val savedUrl = prefs.getString("relay_url", AppState.DEFAULT_RELAY_URL) ?: AppState.DEFAULT_RELAY_URL
        val savedVipKey = prefs.getString("vip_key", "") ?: ""
        val savedAdmKey = prefs.getString("adm_key", "") ?: ""
        val isVip = prefs.getBoolean("is_vip", false)
        val isAdm = prefs.getBoolean("is_adm", false)
        val savedFavorites = prefs.getStringSet("favorites", emptySet()) ?: emptySet()

        repository = RelayRepository(savedUrl)
        _state.value = _state.value.copy(
            relayUrl = savedUrl,
            vipKey = savedVipKey,
            admKey = savedAdmKey,
            isVipActive = isVip || isAdm,
            isAdminActive = isAdm,
            favoriteCommands = savedFavorites
        )
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                fetchUsers()
                delay(2000)
            }
        }
    }

    fun stopAutoRefresh() {
        refreshJob?.cancel()
    }

    private suspend fun fetchUsers() {
        val result = repository.fetchData()
        result.onSuccess { data ->
            _state.value = _state.value.copy(
                users = data.users,
                error = null,
                isLoading = false
            )
        }.onFailure { e ->
            _state.value = _state.value.copy(
                error = "Connection error: ${e.message}",
                isLoading = false
            )
        }
    }

    fun selectUser(user: UserInfo) {
        _state.value = _state.value.copy(selectedUser = user)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedUser = null)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun sendCommand(action: String, params: Map<String, String> = emptyMap()) {
        val state = _state.value
        val user = state.selectedUser ?: return
        val vipHash = if (state.isVipActive) CryptoUtil.sha256(state.vipKey) else null
        val admHash = if (state.isAdminActive) CryptoUtil.sha256(state.admKey) else null

        viewModelScope.launch {
            _state.value = _state.value.copy(lastCommandResult = null)
            val result = repository.sendCommand(user.name, action, params, vipHash, admHash)
            result.onSuccess {
                val entry = HistoryEntry(action = action, target = user.name)
                val history = (_state.value.commandHistory + entry).takeLast(50)
                _state.value = _state.value.copy(
                    lastCommandResult = "✓ $action → ${user.name}",
                    commandHistory = history
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(lastCommandResult = "✗ Ошибка: ${e.message}")
            }
        }
    }

    fun runCombo(combo: ComboCommand) {
        val state = _state.value
        val user = state.selectedUser ?: return
        if (state.isComboRunning) return

        val vipHash = if (state.isVipActive) CryptoUtil.sha256(state.vipKey) else null
        val admHash = if (state.isAdminActive) CryptoUtil.sha256(state.admKey) else null

        viewModelScope.launch {
            _state.value = _state.value.copy(isComboRunning = true, lastCommandResult = "▶ Запуск: ${combo.name}")
            var success = true
            for ((idx, step) in combo.steps.withIndex()) {
                if (step.delayMs > 0 && idx > 0) delay(step.delayMs)
                val r = repository.sendComboStep(user.name, step.action, step.params, vipHash, admHash)
                if (r.isFailure) { success = false; break }
            }
            val msg = if (success) "✓ Комбо '${combo.name}' выполнено" else "✗ Комбо '${combo.name}' прервано"
            val entry = HistoryEntry(action = "combo:${combo.name}", target = user.name)
            _state.value = _state.value.copy(
                isComboRunning = false,
                lastCommandResult = msg,
                commandHistory = (_state.value.commandHistory + entry).takeLast(50)
            )
        }
    }

    fun toggleFavorite(commandId: String) {
        val current = _state.value.favoriteCommands.toMutableSet()
        if (commandId in current) current.remove(commandId) else current.add(commandId)
        prefs.edit().putStringSet("favorites", current).apply()
        _state.value = _state.value.copy(favoriteCommands = current)
    }

    fun activateVip(key: String): Boolean {
        return if (CryptoUtil.verifyVipKey(key)) {
            prefs.edit().putString("vip_key", key).putBoolean("is_vip", true).apply()
            _state.value = _state.value.copy(isVipActive = true, vipKey = key)
            true
        } else false
    }

    fun activateAdmin(key: String): Boolean {
        return if (CryptoUtil.verifyAdminKey(key)) {
            // ADMIN включает все VIP функции
            prefs.edit()
                .putString("adm_key", key).putBoolean("is_adm", true)
                .putBoolean("is_vip", true)
                .apply()
            _state.value = _state.value.copy(isAdminActive = true, admKey = key, isVipActive = true)
            true
        } else false
    }

    fun deactivateVip() {
        prefs.edit().putBoolean("is_vip", false).putString("vip_key", "").apply()
        _state.value = _state.value.copy(isVipActive = false, vipKey = "")
    }

    fun deactivateAdmin() {
        prefs.edit().putBoolean("is_adm", false).putString("adm_key", "").apply()
        _state.value = _state.value.copy(isAdminActive = false, admKey = "")
    }

    fun updateRelayUrl(url: String): Boolean {
        if (!url.startsWith("http://") && !url.startsWith("https://")) return false
        prefs.edit().putString("relay_url", url).apply()
        repository.updateUrl(url)
        _state.value = _state.value.copy(relayUrl = url)
        return true
    }

    fun clearResult() {
        _state.value = _state.value.copy(lastCommandResult = null)
    }
}
