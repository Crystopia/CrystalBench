package net.crystopia.crystalbench.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import net.crystopia.crystalbench.commands.subcommands.GiveItemCommand
import net.crystopia.crystalbench.commands.subcommands.InfoCommand
import net.crystopia.crystalbench.commands.subcommands.PackCommand
import net.crystopia.crystalbench.commands.subcommands.ReloadItemsCommand

object CrystalBenchCommand {

    val command = commandTree("crystalbench", "crystalbench") {
        literalArgument("getItem") {
            stringArgument("item") {
                executes(GiveItemCommand())
            }
        }
        literalArgument("reload") {
            literalArgument("items") {
                executes(ReloadItemsCommand())
            }
            literalArgument("pack") {

            }
            literalArgument("all") {

            }
        }
        literalArgument("pack") {
            executes(PackCommand())
        }
        literalArgument("info") {
            executes(InfoCommand())
        }.register()
    }

}