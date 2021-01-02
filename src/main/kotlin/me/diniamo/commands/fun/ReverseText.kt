package me.diniamo.commands.`fun`

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandContext

class ReverseText : Command(
    "reverse", arrayOf("reversetext"), Category.FUN,
    "Sends the text as the user (via a webhook), reversed", "<text>"
) {
    override fun run(ctx: CommandContext) {
        // Add support with webhooks and stuff

        ctx.channel.sendMessage(reverseText(ctx.args.joinToString(" "))).queue()
    }

    private fun reverseText(text: String): String = StringBuilder(text).reverse().toString()
}