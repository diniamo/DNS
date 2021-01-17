package me.diniamo.commands.`fun`

import me.diniamo.Utils
import me.diniamo.commands.system.*
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.time.OffsetDateTime
import kotlin.math.roundToInt

class FakeDiscord : Command(
    "fakemessage", arrayOf("fd", "fakediscord"), Category.FUN,
    "Creates a fake discord message", "<name>, <message content>, <time stamp>, <ping someone or provide an image (if not it will use your profile picture)>"
) {
    private val discordBG = Color(54, 57, 63)
    private val timeStampColor = Color(114, 118, 125)

    private val nameFont = Font("Whitney-Book", Font.BOLD, 20)
    private val textFont = Font("Whitney-Book", Font.PLAIN, 20)
    private val timeStampFont = Font("Whitney-Book", Font.PLAIN, 15)

    override fun run(ctx: CommandContext) {

        ctx.channel.sendMessage("").flatMap { m -> m.addReaction(ctx.guild!!.getEmoteById(324L)!!) }

        val args = ctx.message.contentRaw.substringAfter(' ').split(", ")

        if(args.size < 3) {
            replyError(ctx, "Not enough arguments.", "Fake Message")
            return
        }

        val finalImg = BufferedImage(482, 98, BufferedImage.TYPE_INT_ARGB)
        val finalGraphics = finalImg.createGraphics().apply {
            println("${getRenderingHint(RenderingHints.KEY_INTERPOLATION)}\n${getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION)}")
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)

            color = discordBG
            clip = null
        }

        val profilePic = getClippedProfilePic(Utils.getImageOrProfilePicture(ctx.message))

        // Image Rendering
        val pfpYPos = finalImg.height / 2 - 32
        finalGraphics.fillRect(0, 0, finalImg.width, finalImg.height)
        finalGraphics.drawImage(profilePic, 20, pfpYPos, 52, 52,null, null)

        // Text rendering
        finalGraphics.renderingHints.remove(RenderingHints.KEY_INTERPOLATION)
        finalGraphics.renderingHints.remove(RenderingHints.KEY_ALPHA_INTERPOLATION)

        finalGraphics.color = Color.WHITE
        finalGraphics.font = nameFont
        finalGraphics.drawString(args[0], 100, pfpYPos + 20)

        finalGraphics.color = timeStampColor
        finalGraphics.drawString(args[1], 100 + (finalGraphics.fontMetrics.getStringBounds(args[0], finalGraphics).width.roundToInt() + 10)
            .also { finalGraphics.font = timeStampFont }, pfpYPos + 20)

        finalGraphics.color = Color.WHITE
        finalGraphics.font = textFont
        finalGraphics.drawString(args[2], 100, pfpYPos + 50)

        ctx.channel.sendFile(Utils.encodePNG(finalImg), "message.png").queue { msg ->
            CommandClient.answerCache[ctx.message.idLong] = MessageData(msg.idLong, OffsetDateTime.now())
        }
        finalGraphics.dispose()
    }

    private fun getClippedProfilePic(img: BufferedImage): BufferedImage = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB).apply {
        val graphics = createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }

        graphics.clip = Ellipse2D.Float(0f, 0f, width.toFloat(), height.toFloat())
        graphics.drawImage(img, 0, 0, width, height, null, null)

        graphics.dispose()
    }
}