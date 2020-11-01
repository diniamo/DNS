package me.diniamo.commands

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.entities.MessageEmbed
import java.lang.management.ManagementFactory

class Info : MyCommand(
    "information", arrayOf("info"), Category.INFO,
    "Shows information about the bot."
) {
    override fun execute(ctx: CommandContext) {
        val runtime = Runtime.getRuntime()

        reply(
            ctx, arrayOf(
                MessageEmbed.Field("JVM Version:", System.getProperty("java.version"), true),
                MessageEmbed.Field("Total Guilds:", ctx.jda.guildCache.size().toString(), true),
                MessageEmbed.Field("Total Users:", Utils.getUserCount(ctx.jda).toString(), true),
                MessageEmbed.Field(
                    "Memory Usage:",
                    (runtime.totalMemory() - runtime.freeMemory() shr 20).toString() + "MB / " + (runtime.maxMemory() shr 20) + "MB",
                    true
                ),
                MessageEmbed.Field("Thread Count:", ManagementFactory.getThreadMXBean().threadCount.toString(), true)
            ), "DNS Information"
        )
    }
}