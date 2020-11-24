package me.diniamo.commands.info

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import net.dv8tion.jda.api.entities.MessageEmbed

class Ping : Command(
    "ping", arrayOf(), Category.INFO, "Shows the ping of the bot"
) {
    override fun run(ctx: CommandContext) {
        ctx.jda.restPing.queue { restPing ->
            reply(ctx, arrayOf(
                MessageEmbed.Field("REST ping:", "${restPing}ms", true),
                MessageEmbed.Field("Gateway ping:", "${ctx.jda.gatewayPing}ms", false)
            ), "DNS ping")
        }
    }
}