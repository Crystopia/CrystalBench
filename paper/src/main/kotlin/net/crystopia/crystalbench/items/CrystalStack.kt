package net.crystopia.crystalbench.items

import gg.flyte.twilight.event.event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CrystalStack(private val handle: ItemStack) : ItemStack() {
    private val interactActions = mutableMapOf<String, PlayerInteractEvent.() -> Unit>()

    private val interactEvent = event<PlayerInteractEvent> {
        if (interactActions.isEmpty()) {
            return@event
        }
        if (item!!.itemMeta.persistentDataContainer.has(CrystalKeys.ID)) {

            val clickedItemId = item!!.itemMeta.persistentDataContainer.get(
                CrystalKeys.ID, PersistentDataType.STRING
            )

            if (clickedItemId != null) {
                if (clickedItemId == id) {
                    interactActions[clickedItemId]!!.invoke(this)
                }
                return@event
            }
            return@event
        }
        return@event
    }

    fun toItemStack(): ItemStack {
        return handle.clone()
    }

    fun interactEvent(action: PlayerInteractEvent.() -> Unit = {}) {
        interactActions[id!!] = action
    }

    val id: String?
        get() = handle.itemMeta?.persistentDataContainer?.get(
            CrystalKeys.ID, org.bukkit.persistence.PersistentDataType.STRING
        )
    val name: String?
        get() = handle.itemMeta?.persistentDataContainer?.get(
            CrystalKeys.NAME, org.bukkit.persistence.PersistentDataType.STRING
        )

}
