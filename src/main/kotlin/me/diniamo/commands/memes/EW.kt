package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils.Companion.videoExecutor
import me.diniamo.Values
import java.io.File


class EW : Command() {
    init {
        name = "ew"
        help = "EW video with specified text."
        arguments = "<text>"
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
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
                            "-vf", "drawtext=\"Impact:text='${event.args.replace("%s", " ")}':fontsize=70:fontcolor=white:x=(w-text_w)/2:y=575\"",
                            "-c:a", "copy", "output.mp4")
                    .start().waitFor()

            event.channel.sendFile(File("output.mp4")).queue()
        }
    }
}