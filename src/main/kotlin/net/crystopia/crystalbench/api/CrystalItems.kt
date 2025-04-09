package net.crystopia.crystalbench.api

import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.models.ItemObject
import net.crystopia.crystalbench.items.ItemParser
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object CrystalItems {
    private var items: MutableMap<String, ItemObject> = mutableMapOf()

    @JvmStatic
    fun loadItems() {
        val map = ConfigManager.loadConfigs()
        items = map.toMutableMap()
    }

    @JvmStatic
    fun itemCount(): Int {
        return items.keys.size
    }

    @JvmStatic
    fun items(): MutableMap<String, ItemObject> {
        return items
    }

    fun getItemObjectById(id: String): ItemStack? {
        val item = items[id]
        return ItemParser(item!!).build()
    }

    fun generateItemStacks(): MutableList<ItemStack> {
        val itemStacks = mutableMapOf<String, ItemStack>()
        items.forEach {
            val itemStack = ItemParser(it.value).build()
            itemStacks[it.key] = itemStack
        }
        return itemStacks.map { it.value }.toMutableList()
    }

    fun getItemObjectByItemStack(itemStack: ItemStack): ItemObject? {
        val itemStackId = itemStack.itemMeta?.persistentDataContainer!!.get(
            NamespacedKey(CrystalBenchPlugin.instance, "id"), PersistentDataType.STRING
        )
        items.forEach { item ->
            if (item.value.id == itemStackId) {
                return item.value
            }
        }
        return null
    }
}



