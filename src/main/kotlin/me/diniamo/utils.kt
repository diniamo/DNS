package me.diniamo

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import javax.imageio.ImageIO
import kotlin.properties.Delegates

const val GREEN_TICK = ":green_tick:772867601901813800"
const val RED_TICK = ":red_tick:772867524995186749"

class Values {
    companion object {
        lateinit var ffmpeg: String
        var answerCacheSizePerGuild = 5
        val avaragePfpColor = Color.decode("#2591cc")

        val arial: Font = Font.createFont(Font.TRUETYPE_FONT, File("./arial.ttf"))
        val impact: Font = Font.createFont(Font.TRUETYPE_FONT, File("./impact.ttf"))
    }
}

class Utils {
    companion object {
        val videoExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val imageExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
        )

        fun toCenterAlignmentX(graphics: Graphics2D, center: Int, text: String): Float = center - graphics.fontMetrics.getStringBounds(text, graphics).width.toFloat() / 2

        fun getUserCount(jda: JDA): Int = jda.guildCache.sumOf { it.memberCount }

        fun formatDurationDHMS(millis: Long): String {
            val duration = Duration.ofMillis(millis)
            return String.format(
                "%sd %s:%s:%s", duration.toDays(),fTime(duration.toHoursPart()), fTime(duration.toMinutesPart()), fTime(duration.toSecondsPart())
            )
        }

        fun fTime(time: Int): String = if (time > 9) time.toString() else "0$time"

        fun getMentionedUserOrAuthor(msg: Message): Member {
            return if (msg.mentionedMembers.size > 0) return msg.mentionedMembers[0]
            else msg.member ?: throw IllegalStateException("Message did not have an author")
        }

        fun downloadImageOrProfilePicture(msg: Message): File {
            Files.copy(
                URL(parseImageOrProfilePictureUrl(msg)).openStream(),
                Paths.get("picture.jpg"),
                StandardCopyOption.REPLACE_EXISTING
            )
            return File("picture.jpg")
        }

        fun downloadImage(att: Message.Attachment): File {
            Files.copy(URL(att.url).openStream(), Paths.get("picture.jpg"), StandardCopyOption.REPLACE_EXISTING)
            return File("picture.jpg")
        }

        fun downloadVideo(att: Message.Attachment): File {
            val file = File("video" + att.fileExtension)
            Files.copy(URL(att.url).openStream(), Paths.get(file.name), StandardCopyOption.REPLACE_EXISTING)
            return file
        }

        fun getImageOrProfilePicture(msg: Message): BufferedImage {
            return if (msg.attachments.size > 0 && msg.attachments[0].isImage)
                ImageIO.read(URL(msg.attachments[0].url))
            else if (msg.mentionedUsers.size > 0)
                ImageIO.read(URL(msg.mentionedUsers[0].effectiveAvatarUrl))
            else
                ImageIO.read(URL(msg.author.effectiveAvatarUrl))
        }

        fun getProfilePicture(msg: Message): BufferedImage {
            return if (msg.mentionedUsers.size > 0) ImageIO.read(URL(msg.mentionedUsers[0].effectiveAvatarUrl)) else ImageIO.read(
                URL(
                    msg.author.effectiveAvatarUrl
                )
            )
        }

        fun parseImageOrProfilePictureUrl(msg: Message): String {
            return if (msg.attachments.size > 0 && msg.attachments[0].isImage)
                msg.attachments[0].url
            else if (msg.mentionedUsers.size > 0)
                msg.mentionedUsers[0].effectiveAvatarUrl
            else
                msg.author.effectiveAvatarUrl
        }
    }
}