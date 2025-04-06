import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import net.kyori.adventure.key.Key
import org.apache.commons.lang3.SerializationException
import org.bukkit.enchantments.Enchantment
import org.bukkit.NamespacedKey
import org.bukkit.damage.DamageType
import org.bukkit.inventory.EquipmentSlotGroup

object DamageTypeSerializer : KSerializer<DamageType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EquipmentSlotGroup", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DamageType) {
        encoder.encodeString(value.toString()) // Speichern als String, z.B. "minecraft:sharpness"
    }

    override fun deserialize(decoder: Decoder): DamageType {
        val key = decoder.decodeString()
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).get(Key.key(key))!!
    }
}
