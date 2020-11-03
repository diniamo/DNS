package me.diniamo.commands.memes

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import java.io.File

class Distract : MyCommand(
    "distract", arrayOf(), Category.MEME,
    "Distract someone.", "<ping the user who you want to distract>"
) {
    override fun run(ctx: CommandContext) {
        try {
            ctx.message.mentionedUsers[0].openPrivateChannel().flatMap {
                it.sendMessage("You have been distracted by ||Henry the stickman||")
                    .addFile(File("./templates/distract.mp4"))
            }
                .queue(null, {
                    replyError(ctx, "That user has DMs closed.", "Distract")
                })
            try {
                ctx.message.delete().queue()
            } catch (ex: Exception) {
                replyError(ctx, "Couldn't delete message. (Permission missing `MANAGE_MESSAGES`)", "Distract")
            }
        } catch (ex: IndexOutOfBoundsException) {
            replyError(ctx, "You didn't ping anyone.", "Distract")
        }
    }
}