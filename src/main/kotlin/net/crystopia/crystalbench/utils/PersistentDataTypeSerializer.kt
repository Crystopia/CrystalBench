package net.crystopia.crystalbench.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.persistence.PersistentDataType

object PersistentDataTypeSerializer : KSerializer<PersistentDataType<*, *>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PersistentDataType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PersistentDataType<*, *>) {
        val name = typeToString(value)
        encoder.encodeString(name)
    }

    override fun deserialize(decoder: Decoder): PersistentDataType<*, *> {
        val name = decoder.decodeString().lowercase()
        return stringToType(name) ?: error("Unknown PersistentDataType: $name")
    }

    private fun typeToString(type: PersistentDataType<*, *>): String = when (type) {
        PersistentDataType.BYTE -> "byte"
        PersistentDataType.SHORT -> "short"
        PersistentDataType.INTEGER -> "int"
        PersistentDataType.LONG -> "long"
        PersistentDataType.FLOAT -> "float"
        PersistentDataType.DOUBLE -> "double"
        PersistentDataType.STRING -> "string"
        PersistentDataType.BYTE_ARRAY -> "byte_array"
        PersistentDataType.INTEGER_ARRAY -> "int_array"
        PersistentDataType.TAG_CONTAINER -> "tag_container"
        PersistentDataType.TAG_CONTAINER_ARRAY -> "tag_container_array"
        else -> error("Unsupported PersistentDataType: $type")
    }

    private fun stringToType(name: String): PersistentDataType<*, *>? = when (name) {
        "byte" -> PersistentDataType.BYTE
        "short" -> PersistentDataType.SHORT
        "int" -> PersistentDataType.INTEGER
        "long" -> PersistentDataType.LONG
        "float" -> PersistentDataType.FLOAT
        "double" -> PersistentDataType.DOUBLE
        "string" -> PersistentDataType.STRING
        "byte_array" -> PersistentDataType.BYTE_ARRAY
        "int_array" -> PersistentDataType.INTEGER_ARRAY
        "tag_container" -> PersistentDataType.TAG_CONTAINER
        "tag_container_array" -> PersistentDataType.TAG_CONTAINER_ARRAY
        else -> null
    }
}
