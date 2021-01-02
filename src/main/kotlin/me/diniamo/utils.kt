package me.diniamo

import com.beust.klaxon.Parser
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import okhttp3.OkHttpClient
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
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
import kotlin.math.roundToInt

const val GREEN_TICK = ":green_tick:772867601901813800"
const val RED_CROSS = ":red_tick:772867524995186749"
const val THUMBS_UP = "\uD83D\uDC4D"
const val THUMBS_DOWN = "\uD83D\uDC4E"

object Values {
    val httpClient = OkHttpClient()
    val jsonParser: Parser = Parser.default()

    lateinit var ffmpeg: String
    var answerCacheSizePerGuild = 5
    val averagePfpColor: Color = Color.decode("#2591cc")
}

object Utils {
    val videoContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors()
    )

    fun removeFirst(original: List<String>) = Array(original.size - 1) { i ->
        original[i + 1]
    }
    fun removeFirst(original: Array<String>) = Array(original.size - 1) { i ->
        original[i + 1]
    }

    fun Message.addReactions(vararg reactions: String) {
        for(r in reactions) {
            addReaction(r).queue()
        }
    }
    fun Message.addReactions(vararg reactions: Emote) {
        for(r in reactions) {
            addReaction(r).queue()
        }
    }

    fun encodePNG(img: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream(2048)
        ImageIO.write(img, "PNG", baos)
        return baos.toByteArray()
    }

    fun lerp(a: Float, b: Float, t: Float): Int {
        return ((1 - t) * a + t * b).roundToInt()
    }

    fun toCenterAlignmentX(graphics: Graphics2D, center: Int, text: String): Float =
        center - graphics.fontMetrics.getStringBounds(text, graphics).width.toFloat() / 2
    fun toCenterAlignmentY(graphics: Graphics2D, center: Int, text: String): Float =
        center - graphics.fontMetrics.getStringBounds(text, graphics).height.toFloat() / 2

    fun getUserCount(jda: JDA): Int = jda.guildCache.sumOf { it.memberCount }

    fun formatDurationDHMS(millis: Long): String {
        val duration = Duration.ofMillis(millis)
        return String.format(
            "%sd %s:%s:%s",
            duration.toDays(),
            fTime(duration.toHoursPart()),
            fTime(duration.toMinutesPart()),
            fTime(duration.toSecondsPart())
        )
    }

    fun fTime(time: Int): String = if (time > 9) time.toString() else "0$time"

    fun getMentionedUserOrAuthor(msg: Message): Member {
        return if (msg.mentionedMembers.size > 0) return msg.mentionedMembers[0]
        else msg.member ?: throw IllegalStateException("Message did not have an author")
    }

    fun downloadImageOrProfilePicture(msg: Message): File {
        val parsed = parseImageOrProfilePictureUrl(msg)
        Files.copy(
            URL(parsed.first).openStream(),
            Paths.get("image.${parsed.second}"),
            StandardCopyOption.REPLACE_EXISTING
        )
        return File("image.${parsed.second}")
    }

    fun downloadImage(att: Message.Attachment): File {
        Files.copy(URL(att.url).openStream(), Paths.get("image.${att.fileExtension ?: ""}"), StandardCopyOption.REPLACE_EXISTING)
        return File("image.${att.fileExtension ?: ""}")
    }

    fun downloadFile(att: Message.Attachment): File {
        val file = File("file.${att.fileExtension}")
        Files.copy(att.retrieveInputStream().get(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        return file
    }

    fun downloadVideo(att: Message.Attachment): File {
        val file = File("video" + att.fileExtension)
        Files.copy(URL(att.url).openStream(), Paths.get(file.name), StandardCopyOption.REPLACE_EXISTING)
        return file
    }

    fun getImageOrProfilePicture(msg: Message): BufferedImage =
        ImageIO.read(URL(parseImageOrProfilePictureUrl(msg).first))


    fun getProfilePicture(msg: Message): BufferedImage {
        return if (msg.mentionedUsers.size > 0) ImageIO.read(URL(msg.mentionedUsers[0].effectiveAvatarUrl)) else ImageIO.read(
            URL(
                msg.author.effectiveAvatarUrl
            )
        )
    }

    /*
    first - link
    second - extension
     */
    fun parseImageOrProfilePictureUrl(msg: Message): Pair<String, String> {
        msg.embeds.firstOrNull { it.type == EmbedType.IMAGE }.let {
            return if (msg.attachments.size > 0 && msg.attachments[0].isImage)
                msg.attachments[0].url to (msg.attachments[0].fileExtension ?: "")
            else if (it != null)
                return it.url!! to it.url!!.substringAfterLast('.')
            else if (msg.mentionedUsers.size > 0)
                msg.mentionedUsers[0].effectiveAvatarUrl to msg.mentionedUsers[0].effectiveAvatarUrl.substringAfterLast(
                    '.'
                )
            else
                msg.author.effectiveAvatarUrl to msg.author.effectiveAvatarUrl.substringAfterLast('.')
        }
    }
}