package net.crystopia.crystalbench.config

import net.crystopia.crystalbench.config.models.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
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
                        //colors = mutableListOf(),
                        floats = mutableListOf(10000F),
                        strings = mutableListOf(),
                    ),
                    model = "",
                    castModel = "",
                    chargedModel = "",
                    damagedModel = "",
                    pullingModel = "",
                    blockingModel = "",
                    fireWorkModel = "",
                    itemModel = "minecraft:player_head",
                    parentModel = "",
                    textures = mutableListOf(),
                    namespace = "minecraft",
                ),
                attributeModifiers = mutableMapOf(
                    Attribute.LUCK to AttributeData(
                        operation = 0, slot = EquipmentSlotGroup.HAND, amount = 0.1
                    ), Attribute.ATTACK_DAMAGE to AttributeData(
                        operation = 0, slot = EquipmentSlotGroup.MAINHAND, amount = 1000.0
                    ), Attribute.ATTACK_SPEED to AttributeData(
                        operation = 0, slot = EquipmentSlotGroup.MAINHAND, amount = 10.0
                    )
                ),
                displayName = "<gray>default</gray>",
                material = Material.PLAYER_HEAD,
                color = mutableListOf(255, 133, 89),
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
                    "SHARPNESS" to 1
                ),
                components = ItemComponent(
                    customData = mutableListOf(
                        CustomData(
                            namespace = "test:test", stringData = "sadfd", type = "string"
                        )
                    ),
                    food = Food(
                        eatAlways = true, nutrition = 300, saturation = 120F
                    ),
                    equitable = Equippable(
                        slot = EquipmentSlot.HEAD,
                        swappable = true,
                        damageOnHurt = false,
                        model = "minecraft:test",
                        cameraOverlay = "",
                        equipSound = Sound.ENTITY_HORSE_ANGRY,
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
                    durability = 5,
                    repairable = Material.APPLE,
                    enchantable = 1,
                    hideTooltip = true,
                    glider = true,
                    cooldown = Cooldown(
                        cooldown = 0.4,
                    ),
                    damageResistant = "is_fire"
                )
            )
        )


    }
}