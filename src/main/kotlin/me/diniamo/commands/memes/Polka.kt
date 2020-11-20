package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import net.dv8tion.jda.api.entities.MessageChannel
import java.io.File

class Polka : Command(
    "polka", arrayOf(), Category.MEME,
    "Creates the guy drumming/singing polkka",
    "<the thing/someone that's vibin' (or an image)> <the thing/someone drumming> <the thing that's going up> <start number> <postfix for start number>"
) {
    override fun run(ctx: CommandContext) {
        Utils.videoExecutor.execute {
            val args = ctx.message.contentRaw.substringAfter("${CommandClient.prefix}polka ").split(", ")

            if(ctx.message.attachments.isNotEmpty() && ctx.message.attachments.first().isImage) {
                val image = Utils.downloadImage(ctx.message.attachments.first())
                val isGif = image.extension == "gif"

                val command = mutableListOf(Values.ffmpeg, "-y", "-i", "./templates/polkka.mp4").apply {
                    if(isGif) {
                        add("-ignore_loop")
                        add("0")
                    }
                    addAll(listOf(
                        "-i", image.name, "-filter_complex",
                        "[1:v] scale=260:200 [scaled];[0:v][scaled] overlay=40:270${if(isGif) ":shortest=1" else ""}, drawtext=fontfile=arial.ttf:fontcolor=white:fontsize=26:text='${args[0]}':x=(1100-text_w)/2:y=190, drawtext=fontfile=arial.ttf:fontsize=22:fontcolor=white:x=(1170-text_w)/2:y=380:text='${args[1]}\\: %{frame_num}${args[3]}':start_number=${args[2]}",
                        "-codec:a", "copy", "output.mp4"
                    ))
                }

                executeCommand(command, ctx.channel, ctx.message.idLong)
            } else {
                executeCommand(listOf(
                    Values.ffmpeg, "-y", "-i", "./templates/polkka.mp4", "-vf",
                    "drawtext=fontfile=arial.ttf:fontcolor=black:fontsize=30:text='${args[0]}':x=60:y=300, drawtext=fontfile=arial.ttf:fontcolor=white:fontsize=26:text='${args[1]}':x=(1100-text_w)/2:y=190, drawtext=fontfile=arial.ttf:fontsize=22:fontcolor=white:x=(1170-text_w)/2:y=380:text='${args[2]}\\: %{frame_num}${args[4]}':start_number=${args[3]}",
                    "-codec:a", "copy", "output.mp4"
                ), ctx.channel, ctx.message.idLong)
            }
        }
    }

    private fun executeCommand(command: List<String>, channel: MessageChannel, commandId: Long) {
        ProcessBuilder()
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.to(File("error.txt")))
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .command(command)
            .start().waitFor()

        val msg = channel.sendFile(File("output.mp4")).complete()
        CommandClient.answerCache[commandId] = msg.idLong
    }
}