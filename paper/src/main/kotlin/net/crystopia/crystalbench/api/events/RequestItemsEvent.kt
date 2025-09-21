package net.crystopia.crystalbench.api.events

import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.config.models.items.ItemObject
import net.crystopia.crystalbench.items.CrystalStack
import net.crystopia.crystalbench.items.ItemBuilder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

object RequestItemsEvent : Event() {
    val handlerList = HandlerList()
    fun register(id: String, item: ItemObject) {
        CrystalItems.items[id] = CrystalStack(ItemBuilder(item).build())
    }

    fun getRegistered(): MutableMap<String, CrystalStack> {
        return CrystalItems.items
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}
