package me.diniamo.commands.memes

import me.diniamo.Utils.Companion.downloadImageOrProfilePicture
import me.diniamo.Utils.Companion.videoExecutor
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import java.io.File
import java.util.*

class PutinWalk : Command(
    "putinwalk", arrayOf("pw", "putin-walk"), Category.MEME,
    "Create the putin walk meme with someone's profile picture or an image",
    "<ping (optional, it will use your profile picture) or image>"
) {
    private var lastUser: Long = 0

    override fun run(ctx: CommandContext) {
        videoExecutor.execute {
            val isGif = ctx.message.attachments[0].fileExtension?.toLowerCase(Locale.ROOT) == "gif"
            downloadImageOrProfilePicture(ctx.message)
            ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/PutinWalk.mp4", if(isGif) "-ignore_loop" else "", if(isGif) "0" else "", "-i", "picture.png", "-filter_complex",
                        "[1]scale=240:80[b];[0][b] overlay=(W-w)/2:(H-h)/2-50:enable='between(t,0,20)':shortest=1", "-pix_fmt", "yuv420p",
                        "-c:a", "copy", "output.mp4")
                    .start().waitFor()

            val msg = ctx.channel.sendFile(File("output.mp4")).complete()
            CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
}