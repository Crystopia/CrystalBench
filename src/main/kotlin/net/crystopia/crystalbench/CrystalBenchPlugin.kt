package net.crystopia.crystalbench

import Log
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import gg.flyte.twilight.twilight
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.commands.CrystalBenchCommand
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.LoadDefaultData
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
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
        CrystalItems.loadItems()

    }

    override fun onEnable() {
        CommandAPI.onEnable()

        // Twilight
        val twilight = twilight(this)

        // Commands
        CrystalBenchCommand

        Log.info(
            """Loaded CrystalBench v.${description.version}
  - Server Software Information:
  - Version: ${server.minecraftVersion}
        """.trimIndent()
        )

        Log.info("Loaded CrystalBench Items: ${CrystalItems.itemCount()}")


    }

    override fun onDisable() {
        CommandAPI.onDisable()

        Log.info("Disabling CrystalBench!")
    }

}