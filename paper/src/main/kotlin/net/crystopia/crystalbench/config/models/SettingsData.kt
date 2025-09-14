package net.crystopia.crystalbench.config.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.crystopia.crystalbench.enums.PackHostType
import net.kyori.adventure.resource.ResourcePackStatus

@Serializable
data class SettingsData(
    var Pack: PackHost = PackHost(
        type = PackHostType.SELFHOST, port = 8000, host = "0.0.0.0", path = "plugins/CrystalBench/out"
    ), var dispatch: Dispatch = Dispatch(
        sendOnReload = true, sendOnPreJoin = true, sendOnJoin = true, promt = "Use the Custom Resourcepack"
    ), var handleStatus: MutableMap<ResourcePackStatus, Action>? = mutableMapOf(
        ResourcePackStatus.ACCEPTED to Action(
            message = "Thanks for accepting", kick = false, sendToServer = null
        )
    )

)

@Serializable
data class Dispatch(
    var sendOnReload: Boolean? = true,
    var sendOnJoin: Boolean? = true,
    var sendOnPreJoin: Boolean? = true,
    var promt: String = "Use this custom Resourcepack!",
)

@Serializable
data class PackHost(
    var type: PackHostType = PackHostType.SELFHOST,
    var host: String = "0.0.0.0",
    var port: Int = 8080,
    var path: String = "plugin/CrystalBench/out",
    var descrption : String = ""
)

@Serializable
data class Action(
    var message: String?, var kick: Boolean?, var sendToServer: String?
)


