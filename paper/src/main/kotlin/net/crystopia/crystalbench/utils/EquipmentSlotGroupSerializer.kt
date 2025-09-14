import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.apache.commons.lang3.SerializationException
import org.bukkit.enchantments.Enchantment
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlotGroup

object EquipmentSlotGroupSerializer : KSerializer<EquipmentSlotGroup> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EquipmentSlotGroup", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlotGroup) {
        encoder.encodeString(value.toString()) // Speichern als String, z.B. "minecraft:sharpness"
    }

    override fun deserialize(decoder: Decoder): EquipmentSlotGroup {
        val key = decoder.decodeString()
        return EquipmentSlotGroup.getByName(key)!!
    }
}
