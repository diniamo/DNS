package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Message

class Ping : Command() {
    override fun execute(event: CommandEvent) {
        val time = System.currentTimeMillis()
        event.channel.sendMessage("Calculating ping...").flatMap {
            msg: Message -> msg.editMessage("My Gateway ping is ${event.jda.gatewayPing}ms.\nMy REST ping is ${System.currentTimeMillis() - time}ms.")
        }.queue() //{ msg: Message -> msg.editMessage("My ping is: " + (System.currentTimeMillis() - time) + "ms").queue() }
    }

    init {
        name = "ping"
        help = "Shows the ping of the bot."
    }
}