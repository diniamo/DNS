package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.entities.MessageEmbed

class Ping : MyCommand(
    "ping", arrayOf(), Category.INFO, "Shows the ping of the bot."
) {
    override fun execute(ctx: CommandContext) {
        val time = System.currentTimeMillis()

        ctx.jda.restPing.queue { restPing ->
            reply(ctx, arrayOf(
                MessageEmbed.Field("REST ping:", "${restPing}ms", true),
                MessageEmbed.Field("Gateway ping:", "${ctx.jda.gatewayPing}ms", false)
            ), "DNS ping")
        }
    }
}