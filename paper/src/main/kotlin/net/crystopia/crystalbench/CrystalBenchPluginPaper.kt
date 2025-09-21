package net.crystopia.crystalbench

import Log
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import gg.flyte.twilight.twilight
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.api.events.RequestItemsEvent
import net.crystopia.crystalbench.commands.CrystalBenchCommand
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.LoadDefaultData
import org.bukkit.plugin.java.JavaPlugin

class CrystalBenchPluginPaper : JavaPlugin() {

    companion object {
        lateinit var instance: CrystalBenchPluginPaper
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

        // Twilight
        val twilight = twilight(this)

        CrystalItems.loadItems()
        
        // Load External Items
        val event = RequestItemsEvent
        server.pluginManager.callEvent(event)
        event.getRegistered().forEach { (id, obj) ->
            CrystalItems.registerItem(id, obj)
        }

        // Command
        CrystalBenchCommand

        Log.info(
            """
            Loaded CrystalBench v.${description.version}
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