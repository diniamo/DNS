package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import java.awt.Font
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class AlwaysHasBeen : Command() {
    init {
        name = "alwayshasbeen"
        aliases = arrayOf("ahb")
        help = "Create the always has been meme with an image and next."
        arguments = "<the thing> <you have to provide an image as an attachment>"
        category = Category("Meme")
    }

    //private var charMap = HashMap<Char, Int>()

    override fun execute(event: CommandEvent) {
        Utils.imageExecutor.execute {
            try {
                val image = ImageIO.read(File("./templates/alwaysHasBeen.png"))
                val graphics = image.createGraphics()
                val file = File("output.png")

                graphics.font = Font("Arial", 0, 30)
                /*if(charMap == null) {
                    charMap = HashMap()
                    graphics.fontMetrics.widths.forEachIndexed { i, width ->
                        charMap!![i.toChar()] = width
                    }
                }*/

                graphics.drawImage(ImageIO.read(URL(event.message.attachments[0].url).openStream()), 7, 9, 400, 400, null)
                graphics.drawString("Wait, it's all", 400, 190)
                graphics.fontMetrics.let { metrics ->
                    //graphics.drawString(event.args + "?", 440, (200 + graphics.fontMetrics.height))
                    graphics.drawString(event.args + "?", 475 - metrics.getStringBounds(event.args + "?", graphics).width.toFloat() / 2, (200 + graphics.fontMetrics.height).toFloat())
                }

                ImageIO.write(image, "png", file)
                event.channel.sendFile(file).queue()
            } catch (ex: Exception) {
                event.reply("Something went wrong.")
            }
        }
    }

    /*private fun estimateWidth(s: String): Int {
        var toReturn = 0
        s.forEach { c ->
            toReturn += charMap!![c]!!
        }
        println(toReturn)
        return toReturn
    }*/
}
