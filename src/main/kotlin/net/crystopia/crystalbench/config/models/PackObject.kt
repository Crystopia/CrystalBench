package net.crystopia.crystalbench.config.models

import kotlinx.serialization.Serializable

@Serializable
data class PackObject(
    var parentModel: String? = null,
    var customModelData: CustomModelData,
    var model: String? = null,
    var textures: MutableList<String>? = null,
    var blockingModel: String? = null,
    var pullingModel: String? = null,
    var chargedModel: String? = null,
    var fireWorkModel: String? = null,
    var castModel: String? = null,
    var damagedModel: String? = null,
    var itemModel: String? = null,
)
