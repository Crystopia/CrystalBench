import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.apache.commons.lang3.SerializationException
import org.bukkit.enchantments.Enchantment
import org.bukkit.NamespacedKey

object EnchantmentSerializer : KSerializer<Enchantment> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Enchantment", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Enchantment) {
        encoder.encodeString(value.key.toString()) // Speichern als String, z.B. "minecraft:sharpness"
    }

    override fun deserialize(decoder: Decoder): Enchantment {
        val key = NamespacedKey.fromString(decoder.decodeString())!!
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key) ?: throw SerializationException("Unknown enchantment")
    }
}
