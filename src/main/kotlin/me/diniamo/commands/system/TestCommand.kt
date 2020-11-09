package me.diniamo.commands.system

import me.diniamo.Utils
import java.awt.image.BufferedImage

class TestCommand : Command(
    "test", arrayOf(), Category.ADMIN,
    "The command I use to test stuff.", ownerCommand = true
) {
    override fun run(ctx: CommandContext) {
        val image = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        graphics.drawString("test\ntest", 100, 100)

        ctx.channel.sendFile(
            Utils.encodePNG(image), "test.png"
        ).queue()
    }
}