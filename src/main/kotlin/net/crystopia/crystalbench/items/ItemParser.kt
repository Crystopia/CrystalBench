package net.crystopia.crystalbench.items

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.config.models.CustomData
import net.crystopia.crystalbench.config.models.ItemObject
import net.crystopia.crystalbench.utils.StringListPersistentDataType
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.damage.DamageType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import org.bukkit.inventory.meta.components.EquippableComponent
import org.bukkit.inventory.meta.components.UseCooldownComponent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.tag.DamageTypeTags

class ItemParser(private val itemObject: ItemObject) {
    private val mm = MiniMessage.miniMessage()

    fun build(): ItemStack {
        val material = itemObject.material ?: Material.PAPER
        val item = ItemStack(material)

        val meta = item.itemMeta ?: return item

        meta.persistentDataContainer.set(
            NamespacedKey(CrystalBenchPlugin.instance, "id"), PersistentDataType.STRING, itemObject.id as String
        )

        // Displayname
        meta.displayName(mm.deserialize(itemObject.displayName ?: ""))

        // Lore
        if (!itemObject.lore.isNullOrEmpty()) {
            val lore = itemObject.lore!!.map { line ->
                mm.deserialize(line)
            }
            meta.lore(lore)
        }

        // Color
        itemObject.color?.let { color ->
            if (meta is LeatherArmorMeta && color.size >= 3) {
                meta.setColor(Color.fromRGB(color[0], color[1], color[2]))
            }
            if (meta is PotionMeta) {
                meta.color = Color.fromRGB(color[0], color[1], color[2])

            }
        }

        // Flags
        itemObject.itemFlags?.forEach { flag ->
            try {
                val itemFlag = ItemFlag.valueOf(flag.toString())
                meta.addItemFlags(itemFlag)
            } catch (e: IllegalArgumentException) {
                // ignore invalid flag
            }
        }

        // Enchantments
        itemObject.enchantments?.forEach { (name, level) ->
            val enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(NamespacedKey.minecraft(name.lowercase()))

            if (enchantment != null) {
                meta.addEnchant(enchantment, level, true)
            }
        }
        if (itemObject.disableEnchanting) {
            meta.persistentDataContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "disable_enchanting"), PersistentDataType.BOOLEAN, true
            )
        }

        // Attribute Modifier
        itemObject.attributeModifiers?.forEach { attribute ->
            try {
                val amount = attribute.value.amount
                val operationIndex = attribute.value.operation
                val slotName = attribute.value.slot

                if (amount != null && operationIndex != null && slotName != null) {
                    val operation = AttributeModifier.Operation.values().getOrNull(operationIndex)
                    val slot = try {
                        slotName
                    } catch (e: IllegalArgumentException) {
                        null
                    }

                    if (operation != null && slot != null) {
                        val modifier = AttributeModifier(
                            NamespacedKey(CrystalBenchPlugin.instance, attribute.key.toString()),
                            amount,
                            operation,
                            attribute.value.slot
                        )
                        meta.addAttributeModifier(attribute.key, modifier)
                    }
                }
            } catch (e: Exception) {
                println("Invalid attribute modifier for ${attribute.key}: ${e.message}")
            }
        }

        if (meta is PotionMeta) {
            itemObject.potionEffects?.let { effects ->
                effects.forEach { potionEffect ->
                    meta.addCustomEffect(
                        potionEffect.type.createEffect(potionEffect.duration, potionEffect.amplifier), true
                    )
                }
            }
        }

        // EnchantmentGlintOverride
        meta.setEnchantmentGlintOverride(itemObject.components!!.enchantmentGlintOverride)
        // MaxStackSize
        meta.setMaxStackSize(itemObject.components!!.maxStackSize)
        // Unbreakable
        meta.isUnbreakable = itemObject.unbreakable
        // Durability
        if (meta is org.bukkit.inventory.meta.Damageable) {
            val durability = itemObject.components!!.durability!!
            meta.setMaxDamage(durability)
        }
        // HideTooltip
        meta.isHideTooltip = itemObject.components!!.hideTooltip!!
        // Food
        val foodComponent = itemObject.components?.food
        if (foodComponent != null) {
            val foodComp = meta.food
            foodComp.nutrition = foodComponent.nutrition ?: 0
            foodComp.saturation = foodComponent.saturation ?: 0f
            foodComp.setCanAlwaysEat(foodComponent.eatAlways ?: false)
            meta.setFood(foodComp)
        }
        // Glider
        meta.isGlider = itemObject.components!!.glider != null
        // Repairable
        meta.persistentDataContainer.set(
            NamespacedKey(
                CrystalBenchPlugin.instance,
                "repairable",

                ), PersistentDataType.STRING, itemObject.components!!.repairable.toString()
        )

        // Consumable
        if (itemObject.components?.consumable != null) {
            val consume = itemObject.components?.consumable!!
            val subContainer = Bukkit.getItemFactory().getItemMeta(Material.STONE).persistentDataContainer
            val subContainerEffect = Bukkit.getItemFactory().getItemMeta(Material.STONE).persistentDataContainer

            subContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "amount"),
                PersistentDataType.STRING,
                consume.sound.toString()
            )
            subContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "consumeParticles"),
                PersistentDataType.BOOLEAN,
                consume.consumeParticles!!
            )
            subContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "consumeSeconds"),
                PersistentDataType.DOUBLE,
                consume.consumeSeconds!!
            )
            subContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "animation"), PersistentDataType.STRING, consume.animation!!
            )

            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "applyEffects"),
                StringListPersistentDataType,
                consume.effect!!.applyEffects!!.map { it.toString() })
            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "removeEffects"),
                StringListPersistentDataType,
                consume.effect!!.removeEffects!!.map { it.toString() })
            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "clearAllEffects"),
                StringListPersistentDataType,
                consume.effect!!.clearAllEffects!!.map { it.toString() })
            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "teleportRandomly"),
                PersistentDataType.DOUBLE,
                consume.effect!!.teleportRandomly!!.diameter
            )
            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "playSound"),
                PersistentDataType.STRING,
                consume.effect!!.playSound.toString()
            )
            subContainerEffect.set(
                NamespacedKey(CrystalBenchPlugin.instance, "applyEffects"),
                StringListPersistentDataType,
                consume.effect!!.applyEffects!!.map { it.toString() } // oder .name etc.
            )
            subContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "effect"),
                PersistentDataType.TAG_CONTAINER,
                subContainerEffect
            )

            meta.persistentDataContainer.set(
                NamespacedKey(CrystalBenchPlugin.instance, "consumable"), PersistentDataType.TAG_CONTAINER, subContainer
            )

        }

        // DamageResistant
        itemObject.components?.damageResistant?.let { damageKeyString ->
            meta.setDamageResistant(
                Bukkit.getTag(
                    DamageTypeTags.REGISTRY_DAMAGE_TYPES,
                    NamespacedKey.fromString(itemObject.components?.damageResistant!!)!!,
                    DamageType::class.java
                )
            )
        }

        // Enchantable
        itemObject.components!!.enchantable.let { enchantable ->
            meta.setEnchantable(itemObject.components!!.enchantable)
        }

        // Cooldown
        itemObject.components!!.cooldown.let { cooldown ->
            if (meta is UseCooldownComponent) {
                meta.cooldownSeconds = itemObject.components!!.cooldown!!.cooldown.toFloat()
                meta.cooldownGroup = NamespacedKey.fromString(itemObject.components!!.cooldown!!.group!!)
            }
        }

        // Equitable
        itemObject.components?.equitable?.let { equippable ->

            val equippableComponent: EquippableComponent = item.itemMeta.equippable

            equippableComponent.setSlot(equippable.slot!!)
            equippableComponent.setModel(NamespacedKey.fromString(equippable.model!!))
            equippableComponent.setCameraOverlay(NamespacedKey.fromString(equippable.cameraOverlay!!))
            equippableComponent.setEquipSound(equippable.equipSound)
            equippableComponent.setAllowedEntities(equippable.allowedEntities)
            equippableComponent.setDispensable(equippable.dispensable)
            equippableComponent.setSwappable(equippable.swappable)
            equippableComponent.setDamageOnHurt(equippable.damageOnHurt)
            meta.setEquippable(equippableComponent)
        }

        // CustomModelData
        if (itemObject.pack!!.customModelData != null) {
            val modelDataComponent: CustomModelDataComponent = item.itemMeta.customModelDataComponent
            modelDataComponent.setFloats(itemObject.pack!!.customModelData.floats!!)
            modelDataComponent.setStrings(itemObject.pack!!.customModelData.strings!!)
            modelDataComponent.setFlags(itemObject.pack!!.customModelData.flags!!)
            meta.setCustomModelDataComponent(modelDataComponent)
            //    meta.customModelDataComponent.colors = itemObject.pack!!.customModelData.colors!!
        }

        // ItemModel
        if (itemObject.pack!!.itemModel != null) {
            meta.itemModel = NamespacedKey.fromString(itemObject.pack!!.itemModel!!)
        }

        // CustomData
        if (itemObject.components!!.customData != null) {
            itemObject.components?.customData?.let {
                applyCustomDataToMeta(meta, it)
            }
        }

        item.itemMeta = meta

        // Amount
        item.amount = 1

        return item
    }

    private fun applyCustomDataToMeta(meta: ItemMeta, dataList: List<CustomData>) {
        val container = meta.persistentDataContainer

        dataList.forEach { data ->
            val key = NamespacedKey.fromString(data.namespace) ?: return@forEach

            when (data.type.lowercase()) {
                "string" -> container.set(key, PersistentDataType.STRING, data.stringData!!)
                "int" -> container.set(key, PersistentDataType.INTEGER, data.intData!!)
                "double" -> container.set(key, PersistentDataType.DOUBLE, data.doubleData!!)
                "float" -> container.set(key, PersistentDataType.FLOAT, data.floatData!!)
                "long" -> container.set(key, PersistentDataType.LONG, data.longData!!)
                else -> {
                    println("Unknown PersistentDataType: ${data.type}")
                }
            }
        }
    }


}