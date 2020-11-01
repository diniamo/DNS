package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import java.awt.Font
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class AlwaysHasBeen : MyCommand(
    "alwayshasbeen", arrayOf("ahb"), Category.MEME,
    "Create the always has been meme with an image and next.",
    "<something that always has been> (you have to provide an image as an attachment)"
) {
    //private var charMap = HashMap<Char, Int>()

    override fun execute(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            try {
                val image = ImageIO.read(File("./templates/alwaysHasBeen.png"))
                val graphics = image.createGraphics()
                val file = File("output.png")
                val argsJoined = ctx.args.joinToString(" ") + "?"

                graphics.font = Font("Arial", 0, 30)
                /*if(charMap == null) {
                    charMap = HashMap()
                    graphics.fontMetrics.widths.forEachIndexed { i, width ->
                        charMap!![i.toChar()] = width
                    }
                }*/

                graphics.drawImage(ImageIO.read(URL(ctx.message.attachments[0].url).openStream()), 7, 9, 400, 400, null)
                graphics.drawString("Wait, it's all", 400, 190)
                graphics.fontMetrics.let { metrics ->
                    //graphics.drawString(event.args + "?", 440, (200 + graphics.fontMetrics.height))
                    graphics.drawString(argsJoined, 475 - metrics.getStringBounds(argsJoined, graphics).width.toFloat() / 2, (200 + graphics.fontMetrics.height).toFloat())
                }

                ImageIO.write(image, "png", file)
                ctx.channel.sendFile(file).queue { msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong }
            } catch (ex: Exception) {
                replyError(ctx, "Something went wrong.", "Error")
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
