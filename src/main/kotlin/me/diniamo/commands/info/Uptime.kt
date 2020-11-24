package me.diniamo.commands.info

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import net.dv8tion.jda.api.entities.MessageEmbed
import java.lang.management.ManagementFactory

class Uptime : Command(
    "uptime", arrayOf("ut"), Category.INFO, "Shows the uptime of the bot"
) {
    override fun run(ctx: CommandContext) {
        reply(ctx, arrayOf(MessageEmbed.Field("Uptime:", Utils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().uptime), true)),
            "DNS Uptime")
    }
}