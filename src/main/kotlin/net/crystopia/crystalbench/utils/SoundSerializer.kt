package net.crystopia.crystalbench.utils

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import org.bukkit.Sound

object SoundSerializer : KSerializer<Sound> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Sound", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Sound) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Sound {
        val name = decoder.decodeString().lowercase()
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT)
            .get(Key.key(name))!!
    }
}
