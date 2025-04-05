package net.crystopia.crystalbench

import Log
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.commands.CrystalBenchCommand
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.LoadDefaultData
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CrystalBenchPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: CrystalBenchPlugin
    }

    init {
        instance = this
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true))

        // Load Data
        Log.info("Load Folder Structure")
        LoadDefaultData.loadStructure()
        ConfigManager.settings


    }

    override fun onEnable() {
        CommandAPI.onEnable()

        // Commands
        CrystalBenchCommand

        Log.info(
            """Loaded CrystalBench v.${description.version}
  - Server Software Information:
  - Version: ${server.minecraftVersion}
        """.trimIndent()
        )


        Log.info("Loaded CrystalBench Items: ${CrystalItems.itemCount()}")
        CrystalItems.items().forEach {
           logger.info(it.toString())
        }

    }

    override fun onDisable() {
        CommandAPI.onDisable()

        Log.info("Disabling CrystalBench!")
    }

}