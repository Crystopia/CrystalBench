package net.crystopia.crystalbench

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig
import net.crystopia.crystalshard.paper.core.utils.Log
import org.slf4j.Logger


class CrystalBenchVelocityPlugin @Inject constructor(val logger: Logger, val server: ProxyServer) {

    companion object {
        lateinit var instance: CrystalBenchVelocityPlugin
    }

    val CHANNEL: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create("crystalbench", "main")

    init {
        instance = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        CommandAPI.onLoad(CommandAPIVelocityConfig(server, this))
        CommandAPI.onEnable();

        Log.info(
            """
            ░█▀▀░█▀▄░█░█░█▀▀░▀█▀░█▀█░█░░░█▀▄░█▀▀░█▀█░█▀▀░█░█
            ░█░░░█▀▄░░█░░▀▀█░░█░░█▀█░█░░░█▀▄░█▀▀░█░█░█░░░█▀█
            ░▀▀▀░▀░▀░░▀░░▀▀▀░░▀░░▀░▀░▀▀▀░▀▀░░▀▀▀░▀░▀░▀▀▀░▀░▀        
            Loaded CrystalBench v.0.1.0
            - Server Software Information:
            - Version: ${server.version}
            """.trimIndent()
        )
        
        server.channelRegistrar.register(CHANNEL);
    }

}