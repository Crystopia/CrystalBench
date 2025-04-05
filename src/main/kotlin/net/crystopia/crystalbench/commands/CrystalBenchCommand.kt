package net.crystopia.crystalbench.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import net.crystopia.crystalbench.commands.subcommands.InfoCommand
import net.crystopia.crystalbench.commands.subcommands.PackCommand

object CrystalBenchCommand {

    val command = commandTree("crystalbench", "crystalbench") {
        literalArgument("pack") {
            executes(PackCommand())
        }
        literalArgument("info") {
            executes(InfoCommand())
        }.register()
    }

}