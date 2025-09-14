import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionEffectType

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PotionEffectType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PotionEffectType) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): PotionEffectType {
        val name = decoder.decodeString().uppercase()
        return PotionEffectType.getByName(name)
            ?: throw IllegalArgumentException("Ungültiger PotionEffectType: $name")
    }
}
