package me.diniamo.commands.memes

import kotlinx.coroutines.runBlocking
import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import net.dv8tion.jda.api.entities.EmbedType
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ThisIsWhyIsHateVideoGames : Command(
    "hatevideogames", arrayOf("hatevg"), Category.MEME,
    "Create the \"This is why I hate video games\" meme", "(provide a video)"
) {
    override fun run(ctx: CommandContext) {
        runBlocking(Utils.videoContext) {
            try {
                val video: File = ctx.message.attachments[0].downloadToFile("video.mp4").get()
                    ?: Files.copy(URL(ctx.message.embeds.firstOrNull { it.type == EmbedType.VIDEO }?.url).openStream(), Paths.get("video.mp4"), StandardCopyOption.REPLACE_EXISTING)
                        .let { File("video.mp4") }

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
