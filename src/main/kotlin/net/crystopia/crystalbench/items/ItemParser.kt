package net.crystopia.crystalbench.items

import io.papermc.paper.datacomponent.DataComponentBuilder
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DamageResistant
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.tag.TagKey
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.config.models.Cooldown
import net.crystopia.crystalbench.config.models.ItemObject
import net.crystopia.crystalbench.utils.StringListPersistentDataType
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.AttributeModifier
import org.bukkit.damage.DamageType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.components.EquippableComponent
import org.bukkit.inventory.meta.components.UseCooldownComponent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.tag.DamageTypeTags
import java.lang.reflect.GenericDeclaration
import java.lang.reflect.TypeVariable

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
        itemObject.components?.equippable?.let { equippable ->
            if (meta is EquippableComponent) {
                val comp: EquippableComponent = meta
                equippable.slot?.let { comp.slot = it }
                equippable.model?.let { comp.model = NamespacedKey.fromString(it) }
                equippable.cameraOverlay?.let { comp.cameraOverlay = NamespacedKey.fromString(it) }
                (equippable.equipSound as? Sound)?.let { comp.setEquipSound(it) }
                comp.allowedEntities = equippable.allowedEntities
                comp.isDispensable = equippable.dispensable
                comp.isSwappable = equippable.swappable
                comp.isDamageOnHurt = equippable.damageOnHurt

                meta.setEquippable(comp)
            }
        }

        // CustomModelData
        if (itemObject.pack!!.customModelData != null) {
            meta.customModelDataComponent.floats = itemObject.pack!!.customModelData.floats!!
            meta.customModelDataComponent.strings = itemObject.pack!!.customModelData.strings!!
            meta.customModelDataComponent.flags = itemObject.pack!!.customModelData.flags!!
            //    meta.customModelDataComponent.colors = itemObject.pack!!.customModelData.colors!!
        }

        // ItemMode
        if (itemObject.pack!!.itemModel != null) {
            meta.itemModel = NamespacedKey.fromString(itemObject.pack!!.itemModel!!)
        }
        item.itemMeta = meta

        // Amount
        item.amount = 1

        return item
    }
}