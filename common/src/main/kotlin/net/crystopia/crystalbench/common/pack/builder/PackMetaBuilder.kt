package net.crystopia.crystalbench.common.pack.builder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.crystopia.crystalbench.common.pack.builder.base.JsonParsable

inline fun packMeta(
    description: String,
    packFormat: Int = 75,
    supportedFormats: MutableList<Int> = mutableListOf(75),
    minFormat: MutableList<Int> = mutableListOf(),
    maxFormat: MutableList<Int> = mutableListOf(),
    overlays: MutableList<Overlay>? = null,
    builder: PackMetaBuilder.() -> Unit
) {
    builder.invoke(
        PackMetaBuilder(
            description,
            packFormat,
            supportedFormats,
            minFormat,
            maxFormat,
            overlays
        )
    )
}

class PackMetaBuilder(
    val description: String,
    val packFormat: Int,
    val supportedFormats: MutableList<Int>,
    val minFormat: MutableList<Int> = mutableListOf(),
    val maxFormat: MutableList<Int> = mutableListOf(),
    val overlays: MutableList<Overlay>? = null
) : JsonParsable() {


    @OptIn(ExperimentalSerializationApi::class)
    override fun toJson(): JsonObject {
        return buildJsonObject {
            putJsonObject("pack") {
                put("description", description)
                put("pack_format", packFormat)
                putJsonArray("supported_formats") {
                    addAll(supportedFormats)
                }
                putJsonArray("min_format") {
                    addAll(minFormat)
                }
                putJsonArray("max_format") {
                    addAll(maxFormat)
                }
            }
            if (!overlays.isNullOrEmpty()) {
                putJsonObject("overlays") {
                    putJsonArray("entries") {
                        overlays.forEach { overlay ->
                            add(Json.encodeToJsonElement(overlay))
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class Overlay(
    var directory: String,
    var formats: Int,
    var minFormat: Int,
    var maxFormat: Int
)