package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import java.io.File

class ThisIsWhyIsHateVideoGames : MyCommand(
    "hatevideogames", arrayOf("hatevg"), Category.MEME,
    "Create the \"This is why I hate video games\" meme.", "(provide a video)"
) {
    override fun run(ctx: CommandContext) {
        Utils.videoExecutor.execute {
            try {
                val att = ctx.message.attachments[0]
                val video = att.downloadToFile(File("video.${att.fileExtension}")).get()

                ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/maleFantasy.mkv", "-i", video.name, "-filter_complex",
                        "[1:v]scale=640:360[vs];[0:v][0:a][vs][1:a]concat=n=2:v=1:a=1[v][a]", "-map", "[v]", "-map", "[a]", "-vsync", "0", "output.mp4")
                    .start().waitFor()

                val msg = ctx.channel.sendFile(File("output.mp4")).complete()
                CommandClient.answerCache[ctx.message.idLong] = msg.idLong
            } catch (ex: Exception) {
                replyError(ctx, "Something went wrong.", "Error")
            }
        }
    }
}
