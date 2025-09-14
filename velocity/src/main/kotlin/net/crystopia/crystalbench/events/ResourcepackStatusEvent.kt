package net.crystopia.crystalbench.events

import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import io.ktor.network.sockets.Datagram
import net.crystopia.crystalbench.config.ConfigManager
import net.kyori.adventure.resource.ResourcePackStatus

object ResourcepackStatusEvent {

    fun onResourcepackStatus(event: PlayerResourcePackStatusEvent) {


        when (event.status) {
            PlayerResourcePackStatusEvent.Status.ACCEPTED -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.ACCEPTED)

            }

            PlayerResourcePackStatusEvent.Status.FAILED_RELOAD -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.FAILED_RELOAD)


            }

            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.FAILED_DOWNLOAD)


            }

            PlayerResourcePackStatusEvent.Status.DECLINED -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.DECLINED)

                
            }

            PlayerResourcePackStatusEvent.Status.DISCARDED -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.DISCARDED)

            }

            PlayerResourcePackStatusEvent.Status.DOWNLOADED -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.DOWNLOADED)

            }

            PlayerResourcePackStatusEvent.Status.INVALID_URL -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.INVALID_URL)

            }

            PlayerResourcePackStatusEvent.Status.SUCCESSFUL -> {
                val data = ConfigManager.settings.handleStatus?.get(ResourcePackStatus.SUCCESSFULLY_LOADED)

            }
        }


    }

}