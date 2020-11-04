package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import net.dv8tion.jda.api.EmbedBuilder
import java.util.*

class HelpCommand(private val client: CommandClient) : Command(
    "commands", arrayOf("c", "help", "h"), Category.INFO,
    "Shows the commands of the bot"
) {
    override fun run(ctx: CommandContext) {
        val builder = StringBuilder()
        val lowerArg = if(ctx.args.isNotEmpty()) ctx.args[0].toUpperCase(Locale.ROOT) else {
            replyError(ctx, "You have to choose one of these categories: `${Category.values().joinToString { it.categoryName.toUpperCase(Locale.ROOT) }}`", "Help")
            return
        }

        val category = Category.values().firstOrNull { it.name == lowerArg }

        if(category == null) {
            replyError(ctx, "You can only choose from these categories: `${Category.values().joinToString { it.categoryName.toUpperCase(Locale.ROOT) }}`", "Help")
        } else {
            for(command in client.commandMap.values.filter { it.category == category }.toSet()) {
                builder.append(
                        "**${command.name}** (${command.help}): ${command.arguments}\n"
                )
            }

            reply(ctx, templateBuilder(ctx).setTitle("${category.emoji} ${category.categoryName} Commands").build())
        }
    }
}