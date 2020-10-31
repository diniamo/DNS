package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class HandWithGun : Command() {
    var lastUserPic: Long = 0

    init {
        name = "handwithgun"
        aliases = arrayOf("hwg", "deletethis", "dt")
        arguments = "<ping a user>"
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        val file = File("dt.png")

        if (Utils.getMentionedUserOrAuthor(event.message).idLong == lastUserPic) {
            event.channel.sendFile(file).queue()
        } else {
            Utils.imageExecutor.execute {
                try {
                    val image = Utils.getImageOrProfilePicture(event.message)
                    val graphics = image.createGraphics()

                    //graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, 50,
                    //        (image.width.toFloat() / (32f / 15f)).roundToInt(), (image.height.toFloat() / (64f / 45f)).roundToInt(), null)
                    graphics.drawImage(ImageIO.read(File("templates/gun.png")), 0, lerp(0f, image.height.toFloat(), .41f),
                            (image.width.toFloat() / (128f / 45f)).roundToInt(), (image.height.toFloat() / (128f / 75f)).roundToInt(), null)

                    ImageIO.write(image, "png", file)
                    event.channel.sendFile(file).complete()
                } catch (ex: Exception) {
                    event.reply("Something went wrong.")
                }
            }
        }
    }

    private fun lerp(a: Float, b: Float, t: Float): Int {
        return ((1 - t) * a + t * b).roundToInt()
    }
}