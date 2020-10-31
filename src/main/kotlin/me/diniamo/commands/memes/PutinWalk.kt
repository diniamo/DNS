package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils.Companion.downloadImageOrProfilePicture
import me.diniamo.Utils.Companion.videoExecutor
import me.diniamo.Values
import java.io.File

class PutinWalk : Command() {

    init {
        name = "putinwalk"
        aliases = arrayOf("pw", "putin-walk")
        help = "Create the putin walk meme with someone's profile picture or an image."
        arguments = "<ping (optional, it will use your profile picture)>"
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        videoExecutor.execute {
            val file = File("output.mp4")
            downloadImageOrProfilePicture(event.message)
            ProcessBuilder()
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .command(Values.ffmpeg, "-y", "-i", "./templates/PutinWalk.mp4", "-ignore_loop", "0", "-i", "picture.jpg", "-filter_complex",
                        "\"[1]scale=240:80[b];[0][b] overlay=(W-w)/2:(H-h)/2-50:enable='between(t,0,20)':shortest=1\"", "-pix_fmt", "yuv420p",
                        "-c:a", "copy", "output.mp4")
                    .start().waitFor()
            /*val image = getImageOrProfilePicture(event.message)
            val graphics = image.createGraphics()
            val file = File("output.png")

            graphics.font = Font("Arial", Font.BOLD, 50)
            //graphics.drawString("ass", image.width / 2 - 50, image.height / 2 - 25)
            graphics.drawString("ass", image.width / 2 - 50, image.height / 2 + 25)
            ImageIO.write(image, "png", file)*/

            event.channel.sendFile(file).queue()

            /*val builder = FFmpegBuilder()
                    .setInput("./templates/PutinWalk.mp4") // Filename, or a FFmpegProbeResult
                    .addInput(parseImageOrProfilePicture(event.message))
                    .overrideOutputFiles(true) // Override the output if it exists
                    .addOutput("output.mp4") // Filename for the destination
                    .setFormat("mp4") // Format is inferred from filename, or can be set
                    .disableSubtitle() // No subtiles
                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                    .done()

            ffExecutor.createJob(builder).run()

            event.channel.sendFile(File("/output.mp4"))*/
        }
    }
}