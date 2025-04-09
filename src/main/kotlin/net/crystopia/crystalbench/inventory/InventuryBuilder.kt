package net.crystopia.crystalbench.inventory

import gg.flyte.twilight.gui.GUI.Companion.openInventory
import gg.flyte.twilight.gui.gui
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.items.ItemParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object InventuryBuilder {
    val mm = MiniMessage.miniMessage()
    fun openPagedInventory(player: Player, page: Int = 0) {
        val allItems = CrystalItems.items().map { ItemParser(it.value).build() }
        val itemsPerPage = 4 * 9
        val startIndex = page * itemsPerPage
        val pagedItems = allItems.drop(startIndex).take(itemsPerPage)

        val gui = gui(mm.deserialize("<b><color:#2486ff>CrystalBench</color></b> Site: ${page + 1}"), 5 * 9) {
            pagedItems.forEachIndexed { index, itemStack ->
                set(index, itemStack) {
                    isCancelled = true
                    viewer.inventory.addItem(itemStack.clone())
                }
            }

            if (page > 0) {
                set(4 * 9, ItemStack(Material.ARROW).apply {
                    itemMeta = itemMeta?.apply {
                        displayName(Component.text("§aGo Back", NamedTextColor.GRAY))
                    }
                }) {
                    isCancelled = true
                    openPagedInventory(player, page - 1)
                }
            }

            set(4 * 9 + 4, ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta?.apply {
                    displayName(mm.deserialize("<red>Close Inventory</red>"))
                }
            }) {
                isCancelled = true
                player.closeInventory()
            }

            if ((startIndex + itemsPerPage) < allItems.size) {
                set(4 * 9 + 8, ItemStack(Material.ARROW).apply {
                    itemMeta = itemMeta?.apply {
                        displayName(Component.text("Next Page", NamedTextColor.GRAY))
                    }
                }) {
                    isCancelled = true
                    openPagedInventory(player, page + 1)
                }
            }
        }

        player.openInventory(gui)
    }
}