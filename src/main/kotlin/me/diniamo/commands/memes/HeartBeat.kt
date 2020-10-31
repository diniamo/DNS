package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import java.awt.Font
import java.io.File
import javax.imageio.ImageIO

class HeartBeat : Command() {
    init {
        name = "heartbeat"
        aliases = arrayOf("hb")
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        Utils.imageExecutor.execute {
            val image = ImageIO.read(File("./templates/heartbeat.png"))
            val graphics = image.createGraphics()
            val file = File("output.png")
            //val splittedString = Utils.splitStringAt(event.args, 12)
            val splittedString = event.args.chunked(15)
            //splittedString.forEach { println(it) }

            graphics.font = Font("Arial", Font.PLAIN, 15)
            splittedString.forEachIndexed { i, s ->
                graphics.drawString(s, 110, 165 + 14 * i)
            }

            ImageIO.write(image, "png", file)
            event.channel.sendFile(file).complete()
        }
    }
}