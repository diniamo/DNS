package me.diniamo.commands.memes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.commands.system.*
import java.awt.image.BufferedImage
import java.io.File
import java.time.OffsetDateTime
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class HandWithGun : Command(
    "handwithgun", arrayOf("hwg", "deletethis", "dt"), Category.MEME,
    "Put a hand with a gun on a picture", "<ping a user/provide an image> (optional, if not used it selects your profile picture)"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val img = Utils.getImageOrProfilePicture(ctx.message)
                val image = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
                val graphics = image.createGraphics().apply { drawImage(img, 0, 0, null) }

                //graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, 50,
                //        (image.width.toFloat() / (32f / 15f)).roundToInt(), (image.height.toFloat() / (64f / 45f)).roundToInt(), null)
                graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, Utils.lerp(0f, image.height.toFloat(), .41f),
                        (image.width.toFloat() / (128f / 45f)).roundToInt(), (image.height.toFloat() / (128f / 75f)).roundToInt(), null)

                ctx.channel.sendFile(Utils.encodePNG(image), "hwg.png").queue { msg ->
                    CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
                }

                graphics.dispose()
            } catch (ex: Exception) {
                replyError(ctx, "Something went wrong.", "Error")
            }
        }
    }
}