package me.diniamo.commands.memes

import kotlinx.coroutines.runBlocking
import me.diniamo.Utils.videoContext
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import java.io.File

class EW : Command(
    "ew", arrayOf(), Category.MEME,
    "EW video with specified text", "<text>"
) {
    private var lastText: String? = null

    override fun run(ctx: CommandContext) {
        runBlocking(videoContext) {
            val joinedArgs = ctx.args.joinToString(" ")
            if(lastText == joinedArgs) {
                ctx.channel.sendFile(File("output.mp4")).queue { msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong }

                return@runBlocking
            }

            ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/EW.mp4",
                            "-vf", "drawtext=fontfile=/impact.ttf:text='$joinedArgs':fontsize=70:fontcolor=white:x=(w-text_w)/2:y=575",
                            "-c:a", "copy", "output.mp4")
                    .start().waitFor()

            val msg = ctx.channel.sendFile(File("output.mp4")).complete()
            CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
}