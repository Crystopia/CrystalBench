package net.crystopia.crystalbench.commands.subcommands

import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

class InfoCommand : CommandExecutor {
    override fun run(sender: CommandSender?, args: CommandArguments?) {
        val mm = MiniMessage.miniMessage()
        val message = mm.deserialize(
            "<st><gray>-----------------------------------------</gray></st>\n<b><gradient:#0084ff:#86d425>CrystalBench</gradient></b>\n<i><gray>Version: <version></gray></i>\n<gray><i>Server Version: <serverVersion></i></gray>\n<gray><st>-----------------------------------------</st></gray>".replace(
                "<version>", CrystalBenchPlugin.instance.description.version
            ).replace("<serverVersion", CrystalBenchPlugin.instance.server.version)
        )
        sender!!.sendMessage(message)
    }
}