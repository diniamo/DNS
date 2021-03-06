package me.diniamo.commands.memes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.*
import java.awt.Font
import java.awt.RenderingHints
import java.io.File
import java.net.URL
import java.time.OffsetDateTime
import javax.imageio.ImageIO

class AlwaysHasBeen : Command(
    "alwayshasbeen", arrayOf("ahb"), Category.MEME,
    "Create the always has been meme with an image and text",
    "<something that always has been> (you have to provide an image as an attachment)"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val image = ImageIO.read(File("./templates/alwaysHasBeen.png"))
                val graphics = image.createGraphics()
                val argsJoined = ctx.args.joinToString(" ") + "?"

                graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
                )

                graphics.font = Font("Arial", Font.PLAIN, 30)
                //graphics.font = Values.arial

                graphics.drawImage(ImageIO.read(URL(ctx.message.attachments[0].url).openStream()), 7, 9, 400, 400, null)
                graphics.drawString("Wait, it's all", 400, 190)
                graphics.fontMetrics.let { metrics ->
                    graphics.drawString(argsJoined, Utils.toCenterAlignmentX(graphics, 475, argsJoined), (200 + metrics.height).toFloat())
                }

                ctx.channel.sendFile(Utils.encodePNG(image), "ahb.png").queue { msg ->
                    CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
                }

                graphics.dispose()
            } catch (ex: Exception) {
                replyError(ctx, "Something went wrong.", "Error")
            }
        }
    }
}
