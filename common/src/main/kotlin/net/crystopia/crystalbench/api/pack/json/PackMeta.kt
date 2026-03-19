package net.crystopia.crystalbench.api.pack.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackMeta(
    var pack: Pack,
    var overlays: Overlays
)

@Serializable
data class Pack(
    var description: String,
    @SerialName("pack_format")
    var packFormat: Int,
    @SerialName("supported_formats")
    var supportedFormats: MutableList<Int>,
    @SerialName("min_format")
    var minFormat: MutableList<Int>,
    @SerialName("max_format")
    var maxFormat: MutableList<Int>,
)

@Serializable
data class Overlays(
    var entries: MutableList<Entry>
)

@Serializable
data class Entry(
    var directory: String,
    var formats: MutableList<Int>,
    @SerialName("min_format")
    var minFormat: MutableList<Int>,
    @SerialName("max_format")
    var maxFormat: MutableList<Int>,
)