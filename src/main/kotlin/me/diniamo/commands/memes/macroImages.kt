package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.memes.MacroImage.Companion.macroImage
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class MacroImage : Command(
    "macroimage", arrayOf("macro-image", "macro", "mimg"), Category.MEME,
    "Create a micro image", "(Provide an image) <some text separated by commas>"
) {
    override fun run(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            if(ctx.message.attachments.size < 1 || !ctx.message.attachments[0].isImage) {
                replyError(ctx, "You have to provide an image to use this command!", "Macro Image")
                return@execute
            }

            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")

            macroImage(
                ctx, ImageIO.read(URL(ctx.message.attachments[0].url)), (if(text[0].isEmpty()) null else text[0]) to text[1]
            )
        }
    }

    companion object {
        fun macroImage(ctx: CommandContext, image: BufferedImage, text: Pair<String?, String>) {
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

            val frc = graphics.fontRenderContext
            val xBottom = Utils.toCenterAlignmentX(graphics, image.width / 2, text.second)
            val vector2 = graphics.font.createGlyphVector(frc, text.second)

            if(text.first != null) {
                val vector1 = graphics.font.createGlyphVector(frc, text.first)
                val xTop = Utils.toCenterAlignmentX(graphics, image.width / 2, text.first!!)

                graphics.color = Color.BLACK
                graphics.draw(vector1.getOutline(xTop, 26f))
                graphics.color = Color.WHITE
                graphics.drawString(text.first, xTop, 26f)
            }

            graphics.color = Color.BLACK
            graphics.stroke = BasicStroke(1f)
            graphics.draw(vector2.getOutline(xBottom, image.height - 4f))

            graphics.color = Color.WHITE
            graphics.drawString(text.second, xBottom, image.height - 4f)

            ctx.channel.sendFile(Utils.encodePNG(image), "macro.png").queue { msg ->
                CommandClient.answerCache[ctx.message.idLong] = msg.idLong
            }
        }
    }
}

class WideFish : Command(
    "widefish", arrayOf("wf"), Category.MEME,
    "Creates the \"Wide fish\" meme with some text", "<text separated by `, `>"
) {
    override fun run(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")

            if(text.size < 2) {
                macroImage(ctx, ImageIO.read(File("./templates/widefish.png")),"me when you don't provide" to " enough text splitted by commas")
                return@execute
            }

            macroImage(ctx, ImageIO.read(File("./templates/widefish.png")), (if(text[0].isEmpty()) null else text[0]) to text[1])
        }
    }
}