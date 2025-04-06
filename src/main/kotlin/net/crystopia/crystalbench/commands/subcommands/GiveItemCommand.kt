package net.crystopia.crystalbench.commands.subcommands

import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.crystopia.crystalbench.api.CrystalItems
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GiveItemCommand : CommandExecutor {
    override fun run(p0: CommandSender?, p1: CommandArguments?) {
        val mm = MiniMessage.miniMessage()
        p0 as Player
        val item = CrystalItems.getItemById(p1?.get(0)!!.toString())
        p0.give(item!!)
        val displayName = item.itemMeta?.displayName()?.let {
            Component.text().append(it).colorIfAbsent(NamedTextColor.WHITE)
        } ?: Component.text(item.type.toString().lowercase().replace('_', ' '))

        p0.sendMessage(mm.deserialize("<green>You get the ").append(displayName).append(mm.deserialize(" item!")))
    }
}