package net.crystopia.crystalbench.commands.subcommands

import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.crystopia.crystalbench.api.CrystalItems
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

class ReloadItemsCommand : CommandExecutor {
    override fun run(p0: CommandSender?, p1: CommandArguments?) {

        CrystalItems.loadItems()
        p0!!.sendMessage(MiniMessage.miniMessage().deserialize("<green>Items reloaded and Updated!"))
    }
}