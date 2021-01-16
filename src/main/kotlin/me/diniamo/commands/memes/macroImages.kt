package me.diniamo.commands.memes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.commands.memes.MacroImage.Companion.macroImage
import me.diniamo.commands.system.*
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.time.OffsetDateTime
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class MacroImage : Command(
    "macroimage", arrayOf("macro-image", "macro", "mimg"), Category.MEME,
    "Create a micro image", "(Provide an image) <some text separated by commas>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")

            //if(attachments[0].url.endsWith(".gif")) macroImageGif(ctx, ImageIO.read(URL(attachments[0].url)), (if(text[0].isEmpty()) null else text[0]) to if(text[1].isEmpty()) null else text[1])
            //else macroImage(ctx, ImageIO.read(URL(attachments[0].url)), (if(text[0].isEmpty()) null else text[0]) to if(text[1].isEmpty()) null else text[1])
            macroImage(ctx, Utils.getImageOrProfilePicture(ctx.message), (if(text[0].isEmpty()) null else text[0]) to if(text[1].isEmpty()) null else text[1])
        }
    }

    companion object {
        fun macroImage(ctx: CommandContext, image: BufferedImage, text: Pair<String?, String?>) {
            val graphics = image.createGraphics()

            graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )
            graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
            )

            graphics.font = Font("Impact", Font.PLAIN, (image.height * 0.13761).roundToInt())
            graphics.stroke = BasicStroke(graphics.font.size * 0.05f)

            val frc = graphics.fontRenderContext

            if(text.first != null) {
                val vector1 = graphics.font.createGlyphVector(frc, text.first)
                val xTop = Utils.toCenterAlignmentX(graphics, image.width / 2, text.first!!)
                val yTop = -4f + graphics.font.size.toFloat()

                graphics.color = Color.BLACK
                graphics.draw(vector1.getOutline(xTop, yTop))
                graphics.color = Color.WHITE
                graphics.drawString(text.first, xTop, yTop)
            }

            if(text.second != null) {
                val xBottom = Utils.toCenterAlignmentX(graphics, image.width / 2, text.second!!)
                val yBottom = (image.height - (graphics.font.size / 7.5f))
                val vector2 = graphics.font.createGlyphVector(frc, text.second)

                graphics.color = Color.BLACK
                graphics.draw(vector2.getOutline(xBottom, yBottom))

                graphics.color = Color.WHITE
                graphics.drawString(text.second, xBottom, yBottom)
            }

            ctx.channel.sendFile(Utils.encodePNG(image), "macro.png").queue { msg ->
                CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
            }

            graphics.dispose()
        }
    }
}

class WideFish : Command(
    "widefish", arrayOf("wf"), Category.MEME,
    "Creates the \"Wide fish\" meme with some text", "<text separated by `, `>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")

            if(text.size < 2) {
                macroImage(ctx, ImageIO.read(File("./templates/widefish.png")),"me when you don't provide" to " enough text split by commas")
                return@launch
            }

            macroImage(ctx, ImageIO.read(File("./templates/widefish.png")), (if(text[0].isEmpty()) null else text[0]) to if(text[1].isEmpty()) null else text[1])
        }
    }
}