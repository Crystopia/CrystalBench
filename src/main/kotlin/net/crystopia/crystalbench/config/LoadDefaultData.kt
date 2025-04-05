package net.crystopia.crystalbench.config

import net.crystopia.crystalbench.config.models.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffectType
import java.io.File

object LoadDefaultData {

    fun loadStructure() {
        val pack = File("plugins/CrystalBench/pack")
        val configs = File("plugins/CrystalBench/configs")
        val sound = File("plugins/CrystalBench/sound.json")

        // Pack Data
        val assets = File("plugins/CrystalBench/pack/assets")
        val buildedPackFolder = File("plugins/CrystalBench/out")
        if (!buildedPackFolder.exists()) {
            buildedPackFolder.mkdirs()
        }
        if (!assets.exists()) {
            assets.mkdirs()
        }

        if (!pack.exists()) {
            pack.mkdirs()

        }
        if (!configs.exists()) {
            configs.mkdirs()

        }
        if (!sound.exists()) {
            sound.createNewFile()
        }


        ConfigManager.saveConfig(
            "default", "default", ItemObject(
                name = "default",
                lore = mutableListOf(""),
                id = "default",
                excludeFromInventory = false,
                itemFlags = mutableListOf(ItemFlag.HIDE_DYE),
                pack = PackObject(
                    customModelData = CustomModelData(
                        flags = mutableListOf(),
                        colors = mutableListOf(),
                        floats = mutableListOf(),
                        strings = mutableListOf(),
                    ),
                    model = "",
                    castModel = "",
                    chargedModel = "",
                    damagedModel = "",
                    pullingModel = "",
                    blockingModel = "",
                    fireWorkModel = "",
                    parentModel = "",
                    textures = mutableListOf("test"),
                ),
                attributeModifiers = mutableMapOf(
                    Attribute.LUCK.toString() to AttributeData(
                        operation = 0, slot = EquipmentSlot.HAND, amount = 0.1
                    )
                ),
                displayName = "<gray>default</gray>",
                material = Material.PAPER,
                color = mutableListOf(132, 324, 221),
                disableEnchanting = false,
                unbreakable = false,
                potionEffects = mutableListOf(
                    PotionEffect(
                        type = PotionEffectType.REGENERATION,
                        amplifier = 1,
                        showIcon = false,
                        showParticles = false,
                        probability = 0.0,
                        duration = 1000,
                        ambient = false
                    )
                ),
                enchantments = mutableMapOf(
                    Enchantment.SHARPNESS to 1
                ),
                components = ItemComponent(
                    customModelData = CustomModelData(
                        flags = mutableListOf(),
                        colors = mutableListOf(),
                        floats = mutableListOf(),
                        strings = mutableListOf(),
                    ),
                    food = Food(
                        eatAlways = false, nutrition = 0, saturation = 12
                    ),
                    equitable = Equippable(
                        slot = EquipmentSlot.HAND,
                        swappable = true,
                        damageOnHurt = false,
                        model = "",
                        cameraOverlay = "",
                        equipSound = "minecraft:block.amethyst_block.hit",
                        dispensable = false,
                        allowedEntities = listOf(EntityType.PLAYER)
                    ),
                    enchantmentGlintOverride = false,
                    maxStackSize = 10,
                    consumable = Consumable(
                        animation = "",
                        consumeParticles = false,
                        sound = "minecraft:block.amethyst_block.hit",
                        effect = Effects(
                            playSound = PlaySound(
                                sound = "minecraft:block.amethyst_block.hit",
                            ),
                            teleportRandomly = TeleportRandomly(
                                diameter = 0.0,
                            ),
                            applyEffects = mutableListOf(PotionEffectType.LUCK),
                            removeEffects = mutableListOf(PotionEffectType.REGENERATION),
                            clearAllEffects = mutableListOf(PotionEffectType.REGENERATION),
                        ),
                        consumeSeconds = 0.1
                    ),
                    durability = 100,
                    repairable = Material.PAPER,
                    enchantable = 1,
                    hideTooltip = false,
                    glider = true,
                    cooldown = Cooldown(
                        cooldown = 0.4,
                    ),
                    damageResistant = "isfire"
                )
            )
        )


    }
}