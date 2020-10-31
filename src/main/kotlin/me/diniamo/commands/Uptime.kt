package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent

class Uptime : Command() {
    val startTime = System.currentTimeMillis()

    init {
        name = "uptime"
        aliases = arrayOf("ut")
        help = "Shows the uptime of the bot."
    }

    override fun execute(event: CommandEvent) {
        event.reply(String.format("%1\$tH:%1\$tM:%1\$tS.%1\$tL", System.currentTimeMillis() - startTime))
    }
}