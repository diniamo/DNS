package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.cos

class Bonk : Command(
    "bonk", arrayOf(), Category.MEME,
    "Create the bonk meme with an image or a profile picture",
    "<ping user or provide an image> (optional: if not used it uses your profile picture)"
) {
    override fun run(ctx: CommandContext) {
        Utils.imageExecutor.execute {
            val overlay = ImageIO.read(File("./templates/bonkOverlay.png"))
            val image = BufferedImage(overlay.width, overlay.height, BufferedImage.TYPE_INT_ARGB)
            val graphics = image.createGraphics()

            graphics.drawImage(Utils.getImageOrProfilePicture(ctx.message), 0, 0, image.width, image.height, null)
            graphics.drawImage(overlay, 0, 0, image.width, image.height, null)

            ctx.channel.sendFile(Utils.encodePNG(image), "bonk.png")
                .queue { msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong }
        }
    }
}