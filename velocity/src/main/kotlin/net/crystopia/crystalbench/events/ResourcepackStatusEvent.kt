package net.crystopia.crystalbench.events

import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import com.velocitypowered.api.proxy.Player
import net.crystopia.crystalbench.CrystalBenchVelocity
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.models.Action
import net.kyori.adventure.resource.ResourcePackStatus
import net.kyori.adventure.text.minimessage.MiniMessage

object ResourcepackStatusEvent {

    fun onResourcepackStatus(event: PlayerResourcePackStatusEvent) {
        val statusMap = mapOf(
            PlayerResourcePackStatusEvent.Status.ACCEPTED to ResourcePackStatus.ACCEPTED,
            PlayerResourcePackStatusEvent.Status.FAILED_RELOAD to ResourcePackStatus.FAILED_RELOAD,
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD to ResourcePackStatus.FAILED_DOWNLOAD,
            PlayerResourcePackStatusEvent.Status.DECLINED to ResourcePackStatus.DECLINED,
            PlayerResourcePackStatusEvent.Status.DISCARDED to ResourcePackStatus.DISCARDED,
            PlayerResourcePackStatusEvent.Status.DOWNLOADED to ResourcePackStatus.DOWNLOADED,
            PlayerResourcePackStatusEvent.Status.INVALID_URL to ResourcePackStatus.INVALID_URL,
            PlayerResourcePackStatusEvent.Status.SUCCESSFUL to ResourcePackStatus.SUCCESSFULLY_LOADED
        )

        val resourcePackStatus = statusMap[event.status] ?: return
        val data = ConfigManager.settings.handleStatus?.get(resourcePackStatus)
        handleAction(data, event.player)
    }
}

fun handleAction(data: Action?, player: Player) {
    if (data?.kick == true) {
        player.disconnect(MiniMessage.miniMessage().deserialize(data.message ?: ""))
        return
    }

    data?.sendToServer?.takeIf { it.length >= 2 }?.let { serverName ->
        CrystalBenchVelocity.instance.server.getServer(serverName).ifPresent { server ->
            player.createConnectionRequest(server).fireAndForget()
        }
    }
}
