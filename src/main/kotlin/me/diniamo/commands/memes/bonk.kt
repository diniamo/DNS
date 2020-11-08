package me.diniamo.commands.memes

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import java.awt.image.BufferedImage
import java.awt.image.Raster
import kotlin.math.PI
import kotlin.math.cos

class Bonk : Command(
    "bonk", arrayOf(), Category.MEME,
    "(BROKEN!) Create the bonk meme with an image or a profile picture",
    "<ping user or provide an image> (optional: if not used it uses your profile picture)"
) {
    override fun run(ctx: CommandContext) {
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