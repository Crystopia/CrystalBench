package net.crystopia.crystalbench.api

import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.models.ItemObject

object CrystalItems {
    private var items = ConfigManager.loadConfigs()

    @JvmStatic
    fun itemCount(): Int {
        return items.keys.size
    }

    @JvmStatic
    fun items(): Map<String, ItemObject> {
        return items
    }
}



