package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import java.awt.*
import java.io.File
import javax.imageio.ImageIO

class WideFish : Command(
    "widefish", arrayOf("wf"), Category.MEME,
    "Creates the \"Wide fish\" meme with some text", "<text separated by `, `>"
) {
    override fun run(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")

            if(text.size < 2) {
                process(ctx, listOf("me when you don't provide", " enough text splitted by commas"))
                return@execute
            }

            process(ctx, text)
        }
    }

    private fun process(ctx: CommandContext, text: List<String>) {
        val image = ImageIO.read(File("./templates/widefish.png"))
        val graphics = image.createGraphics()
        val output = File("output.png")

        graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        graphics.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        )

        graphics.font = Font("Impact", Font.PLAIN, 30)

        val frc = graphics.fontRenderContext
        val vector1 = graphics.font.createGlyphVector(frc, text[0])
        val xTop = Utils.toCenterAlignmentX(graphics, 200, text[0])
        val xBottom = Utils.toCenterAlignmentX(graphics, 200, text[1])
        val vector2 = graphics.font.createGlyphVector(frc, text[1])

        graphics.color = Color.BLACK
        graphics.stroke = BasicStroke(1f)
        graphics.draw(vector1.getOutline(xTop, 26f))
        graphics.draw(vector2.getOutline(xBottom, 214f))

        graphics.color = Color.WHITE
        graphics.drawString(text[0], xTop, 26f)
        graphics.drawString(text[1], xBottom, 214f)

        ImageIO.write(image, "png", output)
        val msg = ctx.channel.sendFile(output).complete()
        CommandClient.answerCache[ctx.message.idLong] = msg.idLong
    }
}