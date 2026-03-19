package net.crystopia.crystalbench.api.config

import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/CrystalBench/settings.json")

    // var settings = settingsFile.loadConfig(SettingsData())


    fun save() {
        // settingsFile.writeText(json.encodeToString(settings))
    }

    fun reload() {
        // settings = loadFromFile(settingsFile)
    }

}
