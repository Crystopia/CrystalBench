package net.crystopia.crystalbench.config

import net.crystopia.crystalbench.config.models.SettingsData
import net.crystopia.onlyup.config.json
import net.crystopia.onlyup.config.loadConfig
import net.crystopia.onlyup.config.loadFromFile
import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/CrystalBench/settings.json")

    var settings = settingsFile.loadConfig(SettingsData())
    

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
    }

    fun reload() {
        settings = loadFromFile(settingsFile)
    }

}
