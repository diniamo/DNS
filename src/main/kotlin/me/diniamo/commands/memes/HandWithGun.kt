package me.diniamo.commands.memes

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class HandWithGun : Command(
    "handwithgun", arrayOf("hwg", "deletethis", "dt"), Category.MEME,
    "Put a hand with a gun on a picture", "<ping a user/provide an image> (optional, if not used it selects your profile picture)"
) {
    override fun run(ctx: CommandContext) {
            Utils.imageExecutor.execute {
                try {
                    val image = Utils.getImageOrProfilePicture(ctx.message)
                    val graphics = image.createGraphics()

                    //graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, 50,
                    //        (image.width.toFloat() / (32f / 15f)).roundToInt(), (image.height.toFloat() / (64f / 45f)).roundToInt(), null)
                    graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, Utils.lerp(0f, image.height.toFloat(), .41f),
                            (image.width.toFloat() / (128f / 45f)).roundToInt(), (image.height.toFloat() / (128f / 75f)).roundToInt(), null)

                    ctx.channel.sendFile(Utils.encodePNG(image), "hwg.png").queue { msg ->
                        CommandClient.answerCache[ctx.message.idLong] = msg.idLong
                    }
                } catch (ex: Exception) {
                    replyError(ctx, "Something went wrong.", "Error")
                }
        }
    }


}