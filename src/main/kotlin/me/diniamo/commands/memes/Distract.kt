package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.io.File

class Distract : Command() {
    init {
        name = "distract"
        arguments = "<the user who you want to distract>"
        help = "Distract someone."
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        try {
            event.message.delete().queue()
            event.message.mentionedUsers[0].openPrivateChannel().flatMap { it.sendMessage("You have been distracted by ||Henry the stickman||").addFile(File("./templates/distract.mp4")) }
                    .queue(null, {
                        event.reply("That user has DMs closed.")
                    })
        } catch (ex: IndexOutOfBoundsException) {
            event.reply("You didn't ping anyone.")
        }
    }
}