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
        val lowerArg = if(ctx.args.isNotEmpty()) ctx.args[0].toUpperCase(Locale.ROOT) else {
            reply(ctx, "You have to choose one of these categories: `${Category.values().joinToString { it.name }}`", "DNS Commands")
            return
        }

        val category = Category.values().firstOrNull { it.name == lowerArg }

        if(category == null) {
            val command = client.commandMap[lowerArg]
            if(command == null) {
                replyError(ctx, "You can only choose from these categories (or a command): `${Category.values().joinToString { it.name }}`", "DNS Commands")
                return
            }

            reply(ctx, "Help: ${command.help}" +
                    "Usage: ${CommandClient.prefix}${command.name} ${command.arguments}\n" +
                    "Aliases: ${command.aliases.joinToString("`, `", "`", "`")}", command.name + " Help")
        } else {
            val builder = StringBuilder()
            builder.append("Prefix: **${CommandClient.prefix}**\n\n")

            for(command in client.commandMap.values.filter { it.category == category }.toSet()) {
                builder.append(
                        "**${command.name}** (${command.help}): ${command.arguments}\n"
                )
            }

            reply(ctx, templateBuilder(ctx).setTitle("${category.emoji} ${category.categoryName} Commands").build())
        }
    }
}