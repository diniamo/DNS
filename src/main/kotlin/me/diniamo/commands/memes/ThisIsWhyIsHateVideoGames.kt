package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import me.diniamo.Values
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.io.File

class ThisIsWhyIsHateVideoGames : Command() {
    init {
        name = "thisiswhyihatevideogames"
        aliases = arrayOf("tiwihvg", "hatevideogames", "hatevg")
        help = "Create one of the \"This is why I hate video games\" memes. Execute the command for help."
        arguments = "<subcommand> <provide a video>"
        children = arrayOf<Command>(MaleAppeal())
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        val builder = EmbedBuilder().setColor(Color.decode("#af131a"))
                .setTitle("This is why I hate video games")
                .appendDescription("Command for making \"This is why I hate video games\" memes")
                .addField("Subcommands", "`maleappeal(ml)`", false)
        event.reply(builder.build())
    }
}

private class MaleAppeal : Command() {
    init {
        name = "maleappeal"
        aliases = arrayOf("ml")
        help = "Subcommand of tiwihvg"
        arguments = "<provide a video>"
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        Utils.videoExecutor.execute {
            val att = event.message.attachments[0]
            val video = att.downloadToFile(File("video.${att.fileExtension}")).get()

            ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/maleFantasy.mkv", "-i", video.name, "-filter_complex",
                            "\"[1:v]scale=640:360[vs];[0:v][0:a][vs][1:a]concat=n=2:v=1:a=1[v][a]\"", "-map", "\"[v]\"", "-map", "\"[a]\"", "-vsync", "0", "output.mp4")
                    .start().waitFor()

            event.channel.sendFile(File("output.mp4")).queue()
        }
    }
}
