package net.crystopia.crystalbench.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.commands.subcommands.GiveItemCommand
import net.crystopia.crystalbench.commands.subcommands.InfoCommand
import net.crystopia.crystalbench.commands.subcommands.PackCommand
import net.crystopia.crystalbench.commands.subcommands.ReloadItemsCommand
import net.crystopia.crystalbench.inventory.InventuryBuilder
import org.bukkit.entity.Player

object CrystalBenchCommand {

    val command = commandTree("crystalbench", "crystalbench") {
        literalArgument("getItem") {
            stringArgument("item") {
                replaceSuggestions(ArgumentSuggestions.strings {
                    CrystalItems.items().map { it.key.toString() }.toTypedArray()
                })
                executes(GiveItemCommand())
            }
        }
        literalArgument("inv") {
            executes(CommandExecutor { commandSender, commandArguments ->
                if (commandSender !is Player) return@CommandExecutor
                InventuryBuilder.openPagedInventory(commandSender as Player, 0)
            })
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