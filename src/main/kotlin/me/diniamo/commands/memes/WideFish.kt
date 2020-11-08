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
            val image = ImageIO.read(File("./templates/widefish.png"))
            val graphics = image.createGraphics()
            val text = ctx.message.contentRaw.substringAfter(' ').split(", ")
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
            val vector2 = graphics.font.createGlyphVector(frc, text[1])

            graphics.color = Color.BLACK
            graphics.stroke = BasicStroke(1f)
            graphics.translate(176, 214)
            graphics.draw(vector1.outline)
            graphics.draw(vector2.outline)

            graphics.color = Color.WHITE
            graphics.translate(-176, -214)
            graphics.drawString(text[0], Utils.toCenterAlignmentX(graphics, 200, text[0]), 26f)
            graphics.drawString(text[1], Utils.toCenterAlignmentX(graphics, 200, text[1]), 214f)

            ImageIO.write(image, "png", output)
            graphics.dispose()
            val msg = ctx.channel.sendFile(output).complete()
            CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
}