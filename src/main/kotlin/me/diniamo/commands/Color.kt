package me.diniamo.commands

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Color : MyCommand(
    "color", arrayOf(), Category.UTILITY,
    "Display a color (Hex/RGB)", "<color code Hex(you have to use Hex) or RGB (separated with a space)>"
) {
    override fun run(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            try {
                val args = ctx.args

                val color = if (args[0].startsWith("#")) {
                    Color.decode(ctx.args[0])
                } else {
                    if(args.size > 3) Color(args[0].toInt(), args[1].toInt(), args[2].toInt(), args[3].toInt())
                    else Color(args[0].toInt(), args[1].toInt(), args[2].toInt())
                }

                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
                val graphics = image.createGraphics()

                graphics.color = color
                graphics.drawRect(-1, -1, 129, 129)
                graphics.fillRect(-1, -1, 129, 129)

                val file = File("output.png")
                ImageIO.write(image, "png", file)

                ctx.channel.sendFile(file).queue { msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong }
            } catch (ex: Exception) {
                replyError(ctx, "You didn't provide the right arguments!", "Color")
            }
        }
    }
}