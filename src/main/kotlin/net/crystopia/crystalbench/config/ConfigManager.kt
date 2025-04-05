package net.crystopia.crystalbench.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.crystopia.crystalbench.config.models.Item
import net.crystopia.crystalbench.config.models.ItemObject
import net.crystopia.crystalbench.config.models.SettingsData
import net.crystopia.onlyup.config.json
import net.crystopia.onlyup.config.loadConfig
import net.crystopia.onlyup.config.loadFromFile
import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/CrystalBench/settings.json")
    private val configDirectory = File("plugins/CrystalBench/configs")

    var settings = settingsFile.loadConfig(SettingsData())

    fun loadConfigs(): Map<String, ItemObject> {
        val configs = mutableMapOf<String, ItemObject>()
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        if (configDirectory.exists() && configDirectory.isDirectory) {
            configDirectory.walkTopDown().filter { it.isFile && it.extension == "json" }.forEach { jsonFile ->
                try {
                    println("Loading: ${jsonFile.name}")

                    val jsonContent = jsonFile.readText()

                    val configItem = json.decodeFromString<Item>(jsonContent)
                    println(configItem)
                    configItem.items.forEach { (key, item) ->
                        configs[key] = item
                    }
                } catch (e: Exception) {
                    println("Failed to load config from ${jsonFile.name}: ${e.message}")
                }
            }
        }
        configs.values.forEach { config ->
            println("Loading ${config.name}...")
        }
        println("Loaded ${configs.size} items")
        return configs
    }

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
    }

    fun saveConfig(id: String, fileName: String, itemObject: ItemObject) {
        val jsonFile = File(configDirectory, "$fileName.json")

        val jsonContent = Json.encodeToString(
            mapOf("items" to mapOf(id to itemObject))
        )
        jsonFile.writeText(jsonContent)
    }



    fun reload() {
        settings = loadFromFile(settingsFile)
    }

}
