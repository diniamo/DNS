package me.diniamo.commands.memes

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.awt.image.BufferedImage
import java.awt.image.Raster
import kotlin.math.PI
import kotlin.math.cos


class Bonk : Command() {
    init {
        name = "bonk"
        arguments = "<ping user or attach an image>"
        help = "Create the bonk meme with an image or a profile picture."
        category = Category("Meme")
    }

    override fun execute(event: CommandEvent) {
        /*val output = File("output.png")

        val grid: Warp = WarpGrid(0,
                500,
                2,
                0,
                500,
                1,
                floatArrayOf(-100f, 0f, 500f, 100f, 1000f, 0f, // top line
                        0f, 500f, 500f, 500f, 1000f, 500f))

        val image = JAI.create("output", Utils.getImageOrProfilePicture(event.message), grid)

        ImageIO.write(image, "png", output)
        event.channel.sendFile(output).complete()*/
    }

    private fun warpImage(img: BufferedImage): BufferedImage {
        // Reproject raster
        var pixel: Any? = null

        val from: Raster = img.raster
        val img2 = BufferedImage(img.width, img.height, img.type) // Assuming img2.getType() == img.getType() always
        val raster = img2.raster

        for (y in 0 until img.height) {
            for (x in 0 until img.width) {
                // Color of the pixel
                pixel = from.getDataElements(x, y, pixel)

                // Its new coordinates
                val x1 = (x * cos(y * PI / 180)).toInt()

                // Set X,Y,pixel to the new raster
                raster.setDataElements(x1, y, pixel)
            }
        }

        return img2
    }
}