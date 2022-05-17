package io.github.clmodding.cltools.model.version

enum class ClientVersion(
    val description: String,
    val versionIdentifier: String = "",
    val launcherVersionIdentifier: String = "$versionIdentifier-CraftLandia",
    val downloadedFile: String = launcherVersionIdentifier,
    val macrosModFile: String? = null,
    val mappingsName: String? = null
) {
    LEGACY("1.5.2 Legacy", "1.5", "1.5.2-CraftLandia", mappingsName = "legacy", macrosModFile = "mod_macros_0.9.9_for_1.5.2.litemod"),
    REPLAY("1.5.2 Replay", "Replay", "1.5.2-CraftLandia", mappingsName = "legacy", macrosModFile = "mod_macros_0.9.9_for_1.5.2.litemod"),
    FAREWELL("1.5.2 Farewell", "Farewell", "1.5.2-CraftLandia", mappingsName = "legacy", macrosModFile = "mod_macros_0.9.9_for_1.5.2.litemod"),
    CHRONOS("1.8 Chronos", "1.8", downloadedFile = "mf_CraftLandia");
}