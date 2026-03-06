package com.trollmaster.pro.data.model

enum class CommandCategory(val label: String, val emoji: String) {
    FAVORITES("Избранное", "⭐"),
    KILL("Убийства", "💀"),
    CONTROL("Контроль", "🕹️"),
    MODIFICATIONS("Модификации", "⚡"),
    VISUAL("Визуал", "👁️"),
    SOUNDS("Звуки", "🔊"),
    CHAT("Чат", "💬"),
    COMBO("Комбо", "🎪"),
    TELEPORT("Телепорт", "🌀"),
    VIP("VIP", "👑"),
    ADMIN("ADMIN", "🔑")
}

data class CommandParam(
    val key: String,
    val hint: String,
    val defaultValue: String = ""
)

data class Command(
    val id: String,
    val name: String,
    val emoji: String,
    val category: CommandCategory,
    val params: List<CommandParam> = emptyList(),
    val requiresVip: Boolean = false,
    val requiresAdmin: Boolean = false
)

data class ComboStep(
    val action: String,
    val params: Map<String, String> = emptyMap(),
    val delayMs: Long = 0
)

data class ComboCommand(
    val id: String,
    val name: String,
    val emoji: String,
    val steps: List<ComboStep>
)

val SOUND_PRESETS = listOf(
    "Loud Bass" to "9114005388",
    "Vine Boom" to "6229968865",
    "Jumpscare" to "12222216",
    "Win Error" to "160715357",
    "Fart" to "8799788385",
    "Bruh" to "5922089741",
    "Scary" to "9114214795",
    "MLG Horn" to "1846895498",
    "Sad Trombone" to "143996823",
    "Siren" to "1585349108",
    "Chicken" to "278722060",
    "Nyan Cat" to "265913095",
    "Circus" to "188794535",
    "Oof" to "12222216",
    "Door Bell" to "138090596",
    "Guitar Riff" to "145487017"
)

val CHAT_PRESETS = listOf(
    "I GOT HACKED 😱",
    "IM A NOOB HELP ME",
    "FREE ROBUX → bit.ly/fake",
    "I LOVE YOU ALL ❤️",
    "HELP IM STUCK",
    "SUBSCRIBE TO MY CHANNEL",
    "IM LEAVING FOREVER",
    "WHO WANTS TO BE MY FRIEND"
)

val COMMANDS = listOf(
    // KILL
    Command("kill", "Kill", "💀", CommandCategory.KILL),
    Command("fling", "Fling", "🌪️", CommandCategory.KILL),
    Command("tpsky", "TP Sky", "☁️", CommandCategory.KILL),
    Command("tpvoid", "TP Void", "🕳️", CommandCategory.KILL),
    Command("megasky", "Mega Sky", "🚀", CommandCategory.KILL),
    Command("edge", "Edge", "📐", CommandCategory.KILL),
    Command("explode", "Explode", "💥", CommandCategory.KILL),
    Command("tornadofling", "Tornado Fling", "🌀", CommandCategory.KILL),
    Command("lightning", "Lightning", "⚡", CommandCategory.KILL),
    Command("deathloop", "Death Loop", "🔁", CommandCategory.KILL),
    Command("stopdeathloop", "Stop Death Loop", "⏹️", CommandCategory.KILL),
    Command("reset", "Reset", "🔄", CommandCategory.KILL),

    // CONTROL
    Command("jump", "Jump", "⬆️", CommandCategory.CONTROL),
    Command("sit", "Sit", "🪑", CommandCategory.CONTROL),
    Command("freeze", "Freeze", "🧊", CommandCategory.CONTROL),
    Command("spin", "Spin", "🌀", CommandCategory.CONTROL),
    Command("fastspin", "Fast Spin", "💫", CommandCategory.CONTROL),
    Command("invert", "Invert", "🔃", CommandCategory.CONTROL),
    Command("ragdoll", "Ragdoll", "🪆", CommandCategory.CONTROL),
    Command("forcewalk", "Force Walk", "🚶", CommandCategory.CONTROL),
    Command("dance", "Dance", "💃", CommandCategory.CONTROL),
    Command("seizurewalk", "Seizure Walk", "🤪", CommandCategory.CONTROL),
    Command("magnet", "Magnet", "🧲", CommandCategory.CONTROL),
    Command("botwalk", "Bot Walk", "🤖", CommandCategory.CONTROL),
    Command("lockcam", "Lock Cam", "📷", CommandCategory.CONTROL),
    Command("skydive", "Skydive", "🪂", CommandCategory.CONTROL),
    Command("speedreset", "Speed Reset", "🔄", CommandCategory.CONTROL),

    // MODIFICATIONS
    Command("speed", "Speed", "💨", CommandCategory.MODIFICATIONS,
        params = listOf(CommandParam("speed", "Speed value", "500"))),
    Command("jumppower", "Jump Power", "🦘", CommandCategory.MODIFICATIONS,
        params = listOf(CommandParam("power", "Power value", "100"))),
    Command("giant", "Giant", "🏔️", CommandCategory.MODIFICATIONS,
        params = listOf(CommandParam("scale", "Scale (0.1-10)", "5"))),
    Command("invisible", "Invisible", "👻", CommandCategory.MODIFICATIONS),
    Command("noclip", "No Clip", "👁️", CommandCategory.MODIFICATIONS),
    Command("fly", "Fly", "✈️", CommandCategory.MODIFICATIONS),
    Command("gravity", "Gravity", "🌍", CommandCategory.MODIFICATIONS,
        params = listOf(CommandParam("gravity", "Gravity value", "0"))),

    // VISUAL
    Command("fire", "Fire", "🔥", CommandCategory.VISUAL),
    Command("sparkles", "Sparkles", "✨", CommandCategory.VISUAL),
    Command("smoke", "Smoke", "💨", CommandCategory.VISUAL),
    Command("bubbles", "Bubbles", "🫧", CommandCategory.VISUAL),
    Command("flashlight", "Flashlight", "🔦", CommandCategory.VISUAL),
    Command("trail", "Trail", "🌠", CommandCategory.VISUAL),
    Command("icebody", "Ice Body", "❄️", CommandCategory.VISUAL),
    Command("removeeffects", "Remove Effects", "🚫", CommandCategory.VISUAL),
    Command("seizure", "Seizure", "😵", CommandCategory.VISUAL),
    Command("blur", "Blur", "🌫️", CommandCategory.VISUAL),
    Command("fov", "FOV", "🔭", CommandCategory.VISUAL,
        params = listOf(CommandParam("fov", "FOV value", "120"))),
    Command("blackout", "Blackout", "⬛", CommandCategory.VISUAL),
    Command("redscreen", "Red Screen", "🔴", CommandCategory.VISUAL),
    Command("rainbow", "Rainbow", "🌈", CommandCategory.VISUAL),
    Command("drunk", "Drunk", "🍺", CommandCategory.VISUAL),
    Command("zoom", "Zoom", "🔍", CommandCategory.VISUAL),
    Command("flipcam", "Flip Cam", "🔄", CommandCategory.VISUAL),
    Command("fog", "Fog", "🌫️", CommandCategory.VISUAL),
    Command("removesky", "Remove Sky", "☀️", CommandCategory.VISUAL),
    Command("night", "Night", "🌙", CommandCategory.VISUAL),
    Command("day", "Day", "☀️", CommandCategory.VISUAL),
    Command("shakecam", "Shake Cam", "📳", CommandCategory.VISUAL),
    Command("clearfx", "Clear FX", "✨", CommandCategory.VISUAL),

    // SOUNDS
    Command("sound", "Play Sound", "🔉", CommandCategory.SOUNDS,
        params = listOf(
            CommandParam("id", "Sound ID", "9114005388"),
            CommandParam("volume", "Volume (0-10)", "1")
        )),
    Command("loopsound", "Loop Sound", "🔁", CommandCategory.SOUNDS,
        params = listOf(
            CommandParam("id", "Sound ID", "9114005388"),
            CommandParam("count", "Count", "3")
        )),
    Command("stopsound", "Stop Sound", "🔇", CommandCategory.SOUNDS),

    // CHAT
    Command("chat", "Chat", "💬", CommandCategory.CHAT,
        params = listOf(CommandParam("message", "Message", "Hello!"))),
    Command("chatspam", "Chat Spam", "📢", CommandCategory.CHAT,
        params = listOf(
            CommandParam("message", "Message", "HELLO"),
            CommandParam("count", "Count", "5")
        )),
    Command("chatnumbers", "Chat Numbers", "🔢", CommandCategory.CHAT,
        params = listOf(CommandParam("count", "Count", "10"))),
    Command("botspam", "Bot Spam", "🤖", CommandCategory.CHAT,
        params = listOf(CommandParam("count", "Count", "10"))),
    Command("keyspam", "Key Spam", "⌨️", CommandCategory.CHAT,
        params = listOf(CommandParam("count", "Count", "10"))),

    // TELEPORT
    Command("tp", "Teleport", "🌀", CommandCategory.TELEPORT,
        params = listOf(
            CommandParam("x", "X coordinate", "0"),
            CommandParam("y", "Y coordinate", "50"),
            CommandParam("z", "Z coordinate", "0")
        )),
    Command("randomtp", "Random TP", "🎲", CommandCategory.TELEPORT),
    Command("tpspawn", "TP to Spawn", "🏠", CommandCategory.TELEPORT),
    Command("tploop", "TP Loop", "🔁", CommandCategory.TELEPORT),
    Command("stoptploop", "Stop TP Loop", "⏹️", CommandCategory.TELEPORT),

    // VIP
    Command("vip_blackscreen", "Black Screen", "⬛", CommandCategory.VIP, requiresVip = true),
    Command("vip_banscreen", "Ban Screen", "🚫", CommandCategory.VIP,
        params = listOf(CommandParam("reason", "Reason", "You are banned")), requiresVip = true),
    Command("vip_fulltext", "Full Screen Text", "📺", CommandCategory.VIP,
        params = listOf(
            CommandParam("text", "Text", "HACKED"),
            CommandParam("color", "Color", "255,0,0"),
            CommandParam("size", "Size", "36")
        ), requiresVip = true),
    Command("vip_jumpscare", "Jumpscare", "👹", CommandCategory.VIP, requiresVip = true),
    Command("vip_matrix", "Matrix", "💻", CommandCategory.VIP, requiresVip = true),
    Command("vip_bloodscreen", "Blood Screen", "🩸", CommandCategory.VIP, requiresVip = true),
    Command("vip_realkick", "Real Kick", "👢", CommandCategory.VIP,
        params = listOf(CommandParam("reason", "Reason", "Kicked")), requiresVip = true),
    Command("vip_blockinput", "Block Input", "🔒", CommandCategory.VIP, requiresVip = true),
    Command("vip_unblockinput", "Unblock Input", "🔓", CommandCategory.VIP, requiresVip = true),
    Command("vip_deathloop", "VIP Death Loop", "💀", CommandCategory.VIP, requiresVip = true),
    Command("vip_removeparts", "Remove Parts", "🫙", CommandCategory.VIP, requiresVip = true),
    Command("vip_flatten", "Flatten", "🥞", CommandCategory.VIP, requiresVip = true),
    Command("vip_lagbomb", "Lag Bomb", "💣", CommandCategory.VIP, requiresVip = true),
    Command("vip_megaspam", "Mega Spam", "📣", CommandCategory.VIP,
        params = listOf(
            CommandParam("message", "Message", "HACKED"),
            CommandParam("count", "Count", "20")
        ), requiresVip = true),
    Command("vip_soundbomb", "Sound Bomb", "💥", CommandCategory.VIP, requiresVip = true),
    Command("vip_chaosmachine", "Chaos Machine", "🌪️", CommandCategory.VIP, requiresVip = true),
    Command("vip_fakeerror", "Fake Error", "⚠️", CommandCategory.VIP,
        params = listOf(CommandParam("message", "Error message", "Fatal error occurred")), requiresVip = true),
    Command("vip_timer", "Timer", "⏱️", CommandCategory.VIP,
        params = listOf(
            CommandParam("seconds", "Seconds", "10"),
            CommandParam("message", "Message", "Game over")
        ), requiresVip = true),
    Command("vip_freezescreen", "Freeze Screen", "🧊", CommandCategory.VIP, requiresVip = true),
    Command("vip_circus", "Circus Mode", "🎪", CommandCategory.VIP, requiresVip = true),

    // ADMIN
    Command("adm_iptrack", "IP Track", "🌐", CommandCategory.ADMIN, requiresVip = true, requiresAdmin = true),
    Command("adm_hwidban", "HWID Ban", "🔨", CommandCategory.ADMIN,
        params = listOf(CommandParam("reason", "Reason", "Banned")), requiresVip = true, requiresAdmin = true),
    Command("adm_dataharvest", "Data Harvest", "📊", CommandCategory.ADMIN, requiresVip = true, requiresAdmin = true),
    Command("adm_takeover", "Takeover", "👑", CommandCategory.ADMIN, requiresVip = true, requiresAdmin = true),
    Command("adm_selfdestruct", "Self Destruct", "💥", CommandCategory.ADMIN, requiresVip = true, requiresAdmin = true)
)

val COMBO_COMMANDS = listOf(
    ComboCommand("казнь", "Казнь", "⚔️", listOf(
        ComboStep("freeze", delayMs = 800),
        ComboStep("spin", delayMs = 500),
        ComboStep("sound", mapOf("id" to "9114005388", "volume" to "1"), 1500),
        ComboStep("kill")
    )),
    ComboCommand("дискотека", "Дискотека", "🪩", listOf(
        ComboStep("seizure", delayMs = 300),
        ComboStep("sound", mapOf("id" to "9114005388", "volume" to "1"), 300),
        ComboStep("spin", delayMs = 300),
        ComboStep("rainbow")
    )),
    ComboCommand("похищение", "Похищение", "🛸", listOf(
        ComboStep("tpsky", delayMs = 800),
        ComboStep("freeze", delayMs = 300),
        ComboStep("fog")
    )),
    ComboCommand("карлик", "Карлик", "🧙", listOf(
        ComboStep("giant", mapOf("scale" to "0.2"), 300),
        ComboStep("speed", mapOf("speed" to "500"), 300),
        ComboStep("spin")
    )),
    ComboCommand("призрак", "Призрак", "👻", listOf(
        ComboStep("invisible", delayMs = 300),
        ComboStep("noclip", delayMs = 300),
        ComboStep("fly")
    )),
    ComboCommand("хаос", "Хаос", "🌪️", listOf(
        ComboStep("fling", delayMs = 800),
        ComboStep("sound", mapOf("id" to "6229968865", "volume" to "1"), 300),
        ComboStep("chatspam", mapOf("message" to "AHHHH", "count" to "3"), 300),
        ComboStep("fire")
    )),
    ComboCommand("клоун", "Клоун", "🤡", listOf(
        ComboStep("giant", mapOf("scale" to "8"), 300),
        ComboStep("spin", delayMs = 300),
        ComboStep("sound", mapOf("id" to "188794535", "volume" to "1"))
    )),
    ComboCommand("мучение", "Мучение", "😈", listOf(
        ComboStep("drunk", delayMs = 300),
        ComboStep("seizure", delayMs = 300),
        ComboStep("speed", mapOf("speed" to "5"))
    )),
    ComboCommand("ракета", "Ракета", "🚀", listOf(
        ComboStep("speed", mapOf("speed" to "1000"), 300),
        ComboStep("jumppower", mapOf("power" to "500"))
    )),
    ComboCommand("nightmare", "Nightmare", "😱", listOf(
        ComboStep("blackout", delayMs = 1000),
        ComboStep("sound", mapOf("id" to "12222216", "volume" to "1"), 2000),
        ComboStep("kill")
    )),
    ComboCommand("ад", "Ад", "🔥", listOf(
        ComboStep("redscreen", delayMs = 500),
        ComboStep("fire", delayMs = 300),
        ComboStep("tpvoid")
    )),
    ComboCommand("тюрьма", "Тюрьма", "⛓️", listOf(
        ComboStep("freeze", delayMs = 300),
        ComboStep("fog", delayMs = 300),
        ComboStep("blur")
    ))
)
