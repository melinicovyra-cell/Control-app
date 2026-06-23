package com.trollmaster.pro.data.model

import org.json.JSONArray
import org.json.JSONObject

data class UserInfo(
    val name: String,
    val id: Long,
    val game: Long,
    val alive: Boolean,
    val ts: Long,
    val hp: Int
) {
    val status: UserStatus get() {
        val now = System.currentTimeMillis() / 1000
        return when {
            !alive -> UserStatus.OFFLINE
            (now - ts) > 30 -> UserStatus.AFK
            else -> UserStatus.LIVE
        }
    }
}

enum class UserStatus { LIVE, AFK, OFFLINE }

data class RelayData(
    val users: List<UserInfo>,
    val vipHash: String,
    val admHash: String,
    val commands: Map<String, JSONObject>
) {
    companion object {
        fun fromJson(json: JSONObject): RelayData {
            val users = mutableListOf<UserInfo>()
            val arr: JSONArray? = json.optJSONArray("users")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val u = arr.getJSONObject(i)
                    users.add(
                        UserInfo(
                            name = u.optString("name", "Unknown"),
                            id = u.optLong("id", 0L),
                            game = u.optLong("game", 0L),
                            alive = u.optBoolean("alive", false),
                            ts = u.optLong("ts", 0L),
                            hp = u.optInt("hp", 100)
                        )
                    )
                }
            }
            val commands = mutableMapOf<String, JSONObject>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key.startsWith("cmd_")) {
                    val cmdObj = json.optJSONObject(key)
                    if (cmdObj != null) commands[key] = cmdObj
                }
            }
            return RelayData(
                users = users,
                vipHash = json.optString("vip_hash", ""),
                admHash = json.optString("adm_hash", ""),
                commands = commands
            )
        }
    }
}
