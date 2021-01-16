package me.diniamo.commands.memes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.commands.system.*
import java.awt.Font
import java.awt.RenderingHints
import java.io.File
import java.net.URL
import java.time.OffsetDateTime
import javax.imageio.ImageIO

class HeartBeat : Command(
    "heartbeat", arrayOf("hb"), Category.MEME,
    "Create the \"Heart Beat \"meme",
    "<text>"
) {
    private var lastText: String? = null

    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            val joinedArgs = ctx.args.joinToString(" ")

            if (lastText == joinedArgs) {
                ctx.channel.sendFile(File("output.mp4"))
                    .queue { msg -> CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now()) }

                return@launch
            }

            val image = ImageIO.read(File("./templates/heartbeat.png"))
            val graphics = image.createGraphics()

            if (joinedArgs.trim() == "") {
                if (ctx.message.attachments.size > 0) {
                    graphics.drawImage(ImageIO.read(URL(ctx.message.attachments[0].url)), 110, 151, 110, 62,null)

                    ctx.channel.sendFile(Utils.encodePNG(image), "hb.png").queue { msg ->
                        CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
                    }

                    lastText = null
                } else {
                    replyError(ctx, "You have to provide an image or text.", "Heart Beat")
                }
            } else {
                val splittedString = joinedArgs.chunked(13)

                graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
                )

                graphics.font = Font("Impact", Font.PLAIN, 15)

                val initY = (180 - (splittedString.size - 1) * (graphics.font.size / 2)) + (graphics.font.size / 2)
                splittedString.forEachIndexed { i, s ->
                    graphics.drawString(s, Utils.toCenterAlignmentX(graphics, 160, s), (initY + i * graphics.font.size).toFloat())
                    //graphics.drawString(s, Utils.toCenterAlignmentX(graphics, 110, s), (165 + 14 * i).toFloat())
                }

                ctx.channel.sendFile(Utils.encodePNG(image), "hb.png").queue { msg ->
                    CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
                }

                lastText = joinedArgs
            }

            graphics.dispose()
        }
    }
}