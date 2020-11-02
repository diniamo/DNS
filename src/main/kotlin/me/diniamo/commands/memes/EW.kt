package me.diniamo.commands.memes

import me.diniamo.Utils.Companion.videoExecutor
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import java.io.File


class EW : MyCommand(
    "er", arrayOf(), Category.MEME,
    "EW video with specified text. (no space)", "<text>"
) {
    override fun execute(ctx: CommandContext) {
        videoExecutor.execute {
            //println("drawtext=\"Impact:text='${event.args}':fontsize=70:fontcolor=white:x=(w-text_w)/2:y=575\"")
            //println(event.args.count { it == ' ' })
            ProcessBuilder()
                    /*.redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectInput(ProcessBuilder.Redirect.PIPE)*/
                    .redirectOutput(ProcessBuilder.Redirect.to(File("output.txt")))
                    .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/EW.mp4",
                            "-vf", "drawtext='Impact:text='${ctx.args.joinToString(" ")}':fontsize=70:fontcolor=white:x=(w-text_w)/2:y=575'",
                            "-c:a", "copy", "output.mp4")
                    .start().waitFor()

            ctx.channel.sendFile(File("output.mp4")).queue { msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong }
        }
    }
}