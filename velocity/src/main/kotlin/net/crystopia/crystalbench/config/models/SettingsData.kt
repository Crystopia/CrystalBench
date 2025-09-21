package net.crystopia.crystalbench.config.models

import kotlinx.serialization.Serializable
import net.kyori.adventure.resource.ResourcePackStatus

@Serializable
data class SettingsData(
    var Pack: PackHost = PackHost(
        port = 8000, host = "0.0.0.0", path = "plugins/CrystalBench/out"
    ), var handleStatus: MutableMap<ResourcePackStatus, Action>? = mutableMapOf(
        ResourcePackStatus.ACCEPTED to Action(
            message = "Thanks for accepting", kick = false, sendToServer = null
        )
    )

)

@Serializable
data class PackHost(
    var host: String = "0.0.0.0",
    var port: Int = 8080,
    var path: String = "plugin/CrystalBench/out",
    var prompt: String = "<u><color:#3de8ff>Please accept our Custom Resourcepack</color></u>",
    var downloadURl: String = "",
)

@Serializable
data class Action(
    var message: String?, var kick: Boolean?, var sendToServer: String? = "Lobby"
)


