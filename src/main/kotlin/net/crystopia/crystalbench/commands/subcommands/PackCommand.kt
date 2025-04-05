package net.crystopia.crystalbench.commands.subcommands

import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.resourcepack.ResourcePackManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

class PackCommand : CommandExecutor {
    override fun run(sender: CommandSender?, args: CommandArguments?) {
        val mm = MiniMessage.miniMessage()
        ResourcePackManager.buildPack()
        val message = mm.deserialize("<green>Pack has been saved successfully</green>")
        sender!!.sendMessage(message)
    }
}