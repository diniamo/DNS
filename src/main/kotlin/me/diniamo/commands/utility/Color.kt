package me.diniamo.commands.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.commands.system.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.time.OffsetDateTime

class Color : Command(
    "color", arrayOf(), Category.UTILITY,
    "Display a color (Hex/RGB)", "<color code Hex(you have to use Hex) or RGB (separated with a space)>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
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
                graphics.fillRect(-1, -1, 129, 129)

                ctx.channel.sendFile(Utils.encodePNG(image), "color.png").queue { msg -> CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now()) }

                graphics.dispose()
            } catch (ex: Exception) {
                replyError(ctx, "You didn't provide the right arguments!", "Color")
            }
        }
    }
}