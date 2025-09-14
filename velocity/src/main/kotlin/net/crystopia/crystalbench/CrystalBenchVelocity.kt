package net.crystopia.crystalbench

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig
import net.crystopia.crystalbench.api.PackAPI
import org.slf4j.Logger


class CrystalBenchVelocity @Inject constructor(val logger: Logger, val server: ProxyServer) {

    companion object {
        lateinit var instance: CrystalBenchVelocity
    }

    val CHANNEL: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create("crystalbench", "channel")

    init {
        instance = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        CommandAPI.onLoad(CommandAPIVelocityConfig(server, this))
        CommandAPI.onEnable();
        
        // InIt Resourcepack Stream
        PackAPI.init()
        
        server.channelRegistrar.register(CHANNEL);

        
    }

}