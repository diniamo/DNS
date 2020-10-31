package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.entities.Message

class Ping : MyCommand(
    "ping", arrayOf(), Category.INFO, "Shows the ping of the bot."
) {
    override fun execute(ctx: CommandContext) {
        val time = System.currentTimeMillis()
        ctx.channel.sendMessage("Calculating ping...").flatMap {
            msg: Message -> msg.editMessage("My Gateway ping is ${ctx.jda.gatewayPing}ms.\nMy REST ping is ${System.currentTimeMillis() - time}ms.")
        }.queue() //{ msg: Message -> msg.editMessage("My ping is: " + (System.currentTimeMillis() - time) + "ms").queue() }
    }
}