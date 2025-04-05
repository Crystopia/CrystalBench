package net.crystopia.crystalbench.config.models

import EnchantmentSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag

@Serializable
data class Item(
    val items: Map<String, ItemObject> = mutableMapOf(),
)

@Serializable
data class ItemObject(
    var name: String? = null,
    var id: String? = null,
    var displayName: String? = null,
    var lore: MutableList<String>? = null,
    var material: Material? = null,
    var color: MutableList<Int>? = null,
    var disableEnchanting: Boolean = false,
    var excludeFromInventory: Boolean = false,
    var unbreakable: Boolean = false,
    var itemFlags: MutableList<ItemFlag>? = null,
    var potionEffects: MutableList<PotionEffect>? = null,
    var attributeModifiers: MutableMap<String, AttributeData>? = null,
    val enchantments: MutableMap<@Serializable(with = EnchantmentSerializer::class) Enchantment, Int>? = null,
    var pack: PackObject? = null,
    var components: ItemComponent? = null,
)

@Serializable
data class AttributeData(
    var amount: Double? = null,
    var operation: Int? = null,
    var slot: EquipmentSlot,
)