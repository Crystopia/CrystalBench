package net.crystopia.crystalbench

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIPaperConfig
import net.crystopia.crystalshard.paper.core.crystalshard
import net.crystopia.crystalshard.paper.core.utils.Log
import net.crystopia.crystalshard.paper.custom.smart.SmartEvents
import org.bukkit.plugin.java.JavaPlugin


class CrystalBenchPaperPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: CrystalBenchPaperPlugin
    }

    init {
        instance = this
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIPaperConfig(this).verboseOutput(true))

        // Load Data  
        Log.info("Load Folder Structure")
    }

    override fun onEnable() {
        CommandAPI.onEnable()

        server.pluginManager.registerEvents(SmartEvents, this)
        crystalshard(this)
        val server = this.server



        Log.info(
            """
            ░█▀▀░█▀▄░█░█░█▀▀░▀█▀░█▀█░█░░░█▀▄░█▀▀░█▀█░█▀▀░█░█
            ░█░░░█▀▄░░█░░▀▀█░░█░░█▀█░█░░░█▀▄░█▀▀░█░█░█░░░█▀█
            ░▀▀▀░▀░▀░░▀░░▀▀▀░░▀░░▀░▀░▀▀▀░▀▀░░▀▀▀░▀░▀░▀▀▀░▀░▀        
            Loaded CrystalBench v.${description.version}
            - Server Software Information:
            - Version: ${server.minecraftVersion}
            """.trimIndent()
        )
    }

    override fun onDisable() {
        CommandAPI.onDisable()

        Log.info("Disabling CrystalBench!")
    }
}