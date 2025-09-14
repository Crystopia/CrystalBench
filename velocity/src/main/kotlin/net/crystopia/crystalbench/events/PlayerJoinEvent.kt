package net.crystopia.crystalbench.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import net.crystopia.crystalbench.config.ConfigManager
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.minimessage.MiniMessage
import java.awt.Component
import java.net.URI

object PlayerJoinEvent {

    
    val PACK_INFO: ResourcePackInfo = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create(ConfigManager.settings.Pack.downloadURl))
        .hash("2849ace6aa689a8c610907a41c03537310949294")
        .build()
    
    @Subscribe
    fun onPlayerJoin(event: ServerPostConnectEvent) {

        val request = ResourcePackRequest.resourcePackRequest()
            .packs(PACK_INFO)
            .prompt(MiniMessage.miniMessage().deserialize(ConfigManager.settings.Pack.prompt))
            .required(true)
            .build();
        
        event.player.sendResourcePacks(request);
    }
    
}