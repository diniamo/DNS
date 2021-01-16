package me.diniamo.commands.memes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.AnimatedGifEncoder
import me.diniamo.Utils
import me.diniamo.commands.system.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.awt.image.IndexColorModel
import java.awt.image.Raster
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.OffsetDateTime
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.cos

class Bonk : Command(
    "bonk", arrayOf(), Category.MEME,
    "Create the bonk meme with an image or a profile picture",
    "<ping user or provide an image> (optional: if not used it uses your profile picture)"
) {
    private val transparent = Color(0,0,0,0)

    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            val pfp = Utils.getImageOrProfilePicture(ctx.message)
            val output = ByteArrayOutputStream()


            val image0 = BufferedImage(498, 498, BufferedImage.TYPE_INT_ARGB)
            val image1 = BufferedImage(498, 498, BufferedImage.TYPE_INT_ARGB)

            val g0 = image0.createGraphics().also { it.drawImage(ImageIO.read(File("./templates/bonk0.png")), 0, 0, null) }
            val g1 = image1.createGraphics().also { it.drawImage(ImageIO.read(File("./templates/bonk1.png")), 0, 0, null) }

            g0.drawImage(pfp, 150, 200, 275, 275, transparent, null)
            g1.drawImage(pfp, 50, 245, 275+125, 275-75, transparent, null)

            val encoder = AnimatedGifEncoder()
            encoder.start(output)
            encoder.setRepeat(0)
            encoder.setFrameRate(4f)

            encoder.addFrame(image0)
            encoder.addFrame(image1)
            encoder.finish()

            ctx.channel.sendFile(output.toByteArray(), "bonk.gif")
                .queue { msg -> CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now()) }


            g0.dispose()
            g1.dispose()

            /*val overlay = ImageIO.read(File("./templates/bonkOverlay.png"))
            val image = BufferedImage(overlay.width, overlay.height, BufferedImage.TYPE_INT_ARGB)
            val graphics = image.createGraphics()

            graphics.drawImage(Utils.getImageOrProfilePicture(ctx.message), 0, 0, image.width, image.height, null)
            graphics.drawImage(overlay, 0, 0, image.width, image.height, null)
U
            ctx.channel.sendFile(Utils.encodePNG(image), "bonk.png")
                .queue { msg -> CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now()) }

            graphics.dispose()*/
        }
    }
}