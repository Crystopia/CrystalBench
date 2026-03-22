package net.crystopia.crystalbench.common.pack.builder.base

import kotlinx.serialization.json.JsonObject

abstract class JsonParsable {
    abstract fun toJson(): JsonObject
}