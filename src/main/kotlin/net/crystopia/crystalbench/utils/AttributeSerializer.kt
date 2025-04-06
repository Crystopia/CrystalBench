import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.kyori.adventure.key.Key
import org.apache.commons.lang3.SerializationException
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffectType

object AttributeSerializer : KSerializer<Attribute> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Attribute", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Attribute) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Attribute {
        val name = decoder.decodeString().lowercase()
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)
            .get(Key.key(name))!!
    }
}
