package me.diniamo.commands.memes

import kotlinx.coroutines.runBlocking
import me.diniamo.Utils
import me.diniamo.Utils.downloadImageOrProfilePicture
import me.diniamo.Values
import me.diniamo.commands.system.*
import java.io.File
import java.time.OffsetDateTime

class PutinWalk : Command(
    "putinwalk", arrayOf("pw", "putin-walk"), Category.MEME,
    "Create the putin walk meme with someone's profile picture or an image",
    "<ping (optional, it will use your profile picture) or image>"
) {
    override fun run(ctx: CommandContext) {
        runBlocking(Utils.videoContext) {
            val image = downloadImageOrProfilePicture(ctx.message)
            val isGif = image.extension == "gif"
            val command = mutableListOf(Values.ffmpeg, "-y", "-i", "./templates/PutinWalk.mp4").apply {
                if(isGif) {
                    add("-ignore_loop")
                    add("0")
                }
                addAll(arrayOf(
                    "-i", image.name, "-filter_complex",
                    "[1]scale=240:80[b];[0][b] overlay=(W-w)/2:(H-h)/2-50:enable='between(t,0,20)'${if(isGif) ":shortest=1" else ""}", "-pix_fmt", "yuv420p",
                    "-c:a", "copy", "output.mp4"
                ))
            }
            ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(command)
                    .start().waitFor()

            val msg = ctx.channel.sendFile(File("output.mp4")).complete()
            CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
        }
    }
}