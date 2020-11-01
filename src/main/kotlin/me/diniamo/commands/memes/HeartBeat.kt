package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import java.awt.Font
import java.io.File
import javax.imageio.ImageIO

class HeartBeat : MyCommand(
    "heartbeat", arrayOf("hb"), Category.MEME,
    "Create the \"heart beat \"meme.",
    "<text>"
) {
    override fun execute(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            val image = ImageIO.read(File("./templates/heartbeat.png"))
            val graphics = image.createGraphics()
            val file = File("output.png")
            //val splittedString = Utils.splitStringAt(event.args, 12)
            val splittedString = ctx.args.joinToString(" ").chunked(15)
            //splittedString.forEach { println(it) }

            graphics.font = Font("Arial", Font.PLAIN, 15)
            splittedString.forEachIndexed { i, s ->
                graphics.drawString(s, 110, 165 + 14 * i)
            }

            ImageIO.write(image, "png", file)
            val msg = ctx.channel.sendFile(file).complete()
            CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
}