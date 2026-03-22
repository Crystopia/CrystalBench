package net.crystopia.crystalbench.paper

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIPaperConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.crystopia.crystalbench.common.pack.builder.packMeta
import net.crystopia.crystalshard.common.log.Log
import net.crystopia.crystalshard.paper.core.crystalshard
import net.crystopia.crystalshard.paper.custom.smart.SmartEvents
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


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

        println(
            "░█▀▀░█▀▄░█░█░█▀▀░▀█▀░█▀█░█░░░█▀▄░█▀▀░█▀█░█▀▀░█░█\n" +
            "░█░░░█▀▄░░█░░▀▀█░░█░░█▀█░█░░░█▀▄░█▀▀░█░█░█░░░█▀█\n" +
            "░▀▀▀░▀░▀░░▀░░▀▀▀░░▀░░▀░▀░▀▀▀░▀▀░░▀▀▀░▀░▀░▀▀▀░▀░▀"
        )
        Log.info(
            """
            Loaded CrystalBench v.0.1.0
            - Server Software Information:
                >> Version: ${server.minecraftVersion}
        """.trimIndent()
        )

        packMeta("test") {
            File("plugins/Test/data.json").writeText(Json.encodeToString(toJson()))
        }
    }

    override fun onDisable() {
        CommandAPI.onDisable()

        Log.info("Disabling CrystalBench!")
    }
}