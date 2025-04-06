package net.crystopia.crystalbench.api

import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.items.ItemParser
import org.bukkit.inventory.ItemStack

object CrystalItems {
    private var items: MutableMap<String, ItemStack> = mutableMapOf()


    @JvmStatic
    fun loadItems() {
        val map = ConfigManager.loadConfigs()
        map.values.forEach {
            items.put(
                it.id.toString(), ItemParser(it).build()
            )
        }
    }

    @JvmStatic
    fun itemCount(): Int {
        return items.keys.size
    }

    @JvmStatic
    fun items(): MutableMap<String, ItemStack> {
        return items
    }

    fun getItemById(id: String): ItemStack? {
        return items[id]
    }

    fun getItemByItemStack(itemStack: ItemStack): String? {
        TODO("Coming soon")
    }


}



