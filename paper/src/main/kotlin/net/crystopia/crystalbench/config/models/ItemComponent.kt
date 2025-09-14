package net.crystopia.crystalbench.config.models

import PotionEffectTypeSerializer
import kotlinx.serialization.Serializable
import net.crystopia.crystalbench.utils.SoundSerializer
import org.bukkit.Material
import org.bukkit.Sound
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
    // Need event
    var consumable: Consumable? = null,
    var damageResistant: String? = null,
    // NEED Test
    var enchantable: Int? = null,
    var glider: Boolean? = null,
    // Need event
    var repairable: Material? = null,
    var cooldown: Cooldown? = null,
    var equitable: Equippable? = null,
    var customData: MutableList<CustomData>? = null,
)

@Serializable
data class CustomData(
    var namespace: String,
    var type: String,
    var stringData: String? = null,
    var intData: Int? = null,
    var doubleData: Double? = null,
    var floatData: Float? = null,
    var longData: Long? = null,
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
    var slot: EquipmentSlot? = null,
    val model: String? = null,
    val cameraOverlay: String? = null,
    val equipSound: @Serializable(with = SoundSerializer::class) Sound? = null,
    val allowedEntities: List<EntityType>? = null,
    val dispensable: Boolean = true,
    val swappable: Boolean = true,
    val damageOnHurt: Boolean = false
)

@Serializable
data class Cooldown(
    var cooldown: Double, var group: String? = null,
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
