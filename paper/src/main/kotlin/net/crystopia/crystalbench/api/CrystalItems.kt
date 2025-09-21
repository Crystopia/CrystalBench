package net.crystopia.crystalbench.api

import net.crystopia.crystalbench.CrystalBenchPluginPaper
import net.crystopia.crystalbench.api.events.RequestItemsEvent
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.items.CrystalStack
import net.crystopia.crystalbench.items.ItemBuilder
import org.bukkit.inventory.ItemStack

object CrystalItems {
    var items: MutableMap<String, CrystalStack> = mutableMapOf()

    @JvmStatic
    fun loadItems() {
        val event = RequestItemsEvent
        CrystalBenchPluginPaper.instance.server.pluginManager.callEvent(event)
        event.getRegistered().forEach { (id, obj) ->
            registerItem(id, obj)
        }

        val map = ConfigManager.loadConfigs()
        map.toMutableMap().forEach { (id, stack) ->
            items[id] = CrystalStack(ItemBuilder(stack).build())
        }
    }

    @JvmStatic
    fun itemCount(): Int {
        return items.keys.size
    }

    @JvmStatic
    fun registerItem(name: String, stack: CrystalStack): Boolean {
        if (!items.containsKey(name)) {
            return false
        }
        items[name] = stack
        return true
    }

    @JvmStatic
    fun items(): MutableMap<String, CrystalStack> {
        return items
    }

    fun getItem(id: String): CrystalStack? {
        return items[id]
    }

    fun getItemObjectById(id: String): ItemStack {
        val item = items[id]
        return item!!.toItemStack()
    }
}



