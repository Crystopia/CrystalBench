package net.crystopia.crystalbench.config.models

import DamageTypeSerializer
import PotionEffectTypeSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.damage.DamageType
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffectType

@Serializable
data class ItemComponent(
    var maxStackSize: Int? = null,
    var enchantmentGlintOverride: Boolean? = null,
    var durability: Int? = null,
    var hideTooltip: Boolean? = null,
    var food: Food? = null,
    var consumable: Consumable? = null,
    var damageResistant: String? = null,
    // TODO
    var enchantable: Int? = null,
    var glider: Boolean? = null,
    var cooldown: Cooldown? = null,
    var repairable: Material? = null,
    var equitable: Equippable? = null,
)

@Serializable
data class CustomModelData(
    var floats: MutableList<Float>? = null,
    // SOON
    //  var colors: MutableList<Int>? = null,
    var strings: MutableList<String>? = null,
    var flags: MutableList<Boolean>? = null,
)

@Serializable
data class Equippable(
    val slot: EquipmentSlot? = null,
    val model: String? = null,
    val cameraOverlay: String? = null,
    val equipSound: String? = null,
    val allowedEntities: List<EntityType>? = null,
    val dispensable: Boolean = true,
    val swappable: Boolean = true,
    val damageOnHurt: Boolean = false
)

@Serializable
data class Cooldown(
    var cooldown: Double,
)

@Serializable
data class Food(
    var nutrition: Int? = null,
    var saturation: Float? = null,
    var eatAlways: Boolean? = null,
)

@Serializable
data class Consumable(
    var sound: String? = null,
    var consumeParticles: Boolean? = null,
    var consumeSeconds: Double? = null,
    var animation: String? = null,
    var effect: Effects? = null
)

@Serializable
data class Effects(

    val applyEffects: MutableList<@Serializable(with = PotionEffectTypeSerializer::class) PotionEffectType>? = null,
    val removeEffects: MutableList<@Serializable(with = PotionEffectTypeSerializer::class) PotionEffectType>? = null,
    val clearAllEffects: MutableList<@Serializable(with = PotionEffectTypeSerializer::class) PotionEffectType>? = null,
    val teleportRandomly: TeleportRandomly? = null,
    val playSound: PlaySound? = null
)

@Serializable
data class PotionEffect(
    @Serializable(with = PotionEffectTypeSerializer::class) val type: PotionEffectType,
    val duration: Int,
    val amplifier: Int,
    val ambient: Boolean,
    val showIcon: Boolean,
    val showParticles: Boolean,
    val probability: Double
)

@Serializable
data class TeleportRandomly(
    val diameter: Double
)

@Serializable
data class PlaySound(
    val sound: String
)
