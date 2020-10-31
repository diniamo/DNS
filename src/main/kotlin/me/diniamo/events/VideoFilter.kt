package me.diniamo.events

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.util.*
import javax.annotation.Nonnull

class VideoFilter : ListenerAdapter() {
    private val notFunny = arrayOf("\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDEB", "\uD83C\uDDFA")

    override fun onGuildMessageReceived(@Nonnull event: GuildMessageReceivedEvent) {
        val content = event.message.contentStripped.toLowerCase()
        val msg = event.message
        if (event.message.attachments.isNotEmpty()) {
            var uc: URLConnection
            var `in`: BufferedInputStream
            var file: File
            var out: FileOutputStream
            var dataBuffer: ByteArray
            var bytesRead: Int
            for (att in event.message.attachments) {
                try {
                    uc = URL(att.url).openConnection()
                    uc.addRequestProperty("User-Agent", "Mozilla")
                    `in` = BufferedInputStream(uc.getInputStream())
                    file = File("temp.mp4")
                    out = FileOutputStream(file)
                    dataBuffer = ByteArray(1024)
                    while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                        out.write(dataBuffer, 0, bytesRead)
                    }
                    if (Arrays.equals(
                                    Files.readAllBytes(
                                            file.toPath()),
                                    javaClass.getResourceAsStream("/Putin_Walk.mp4").readAllBytes())) {
                        msg.delete().reason("Bad meme.").queue()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val index1 = content.indexOf("https://cdn.discordapp.com/attachments/")
            val index2 = content.indexOf(".mp4")
            println("InDex1:$index1\nIndex2:$index2")
            if (index1 != -1 && index2 != -1) {
                println(content.substring(index1, index2))
                try {
                    uc = URL(content.substring(index1, index2)).openConnection()
                    uc.addRequestProperty("User-Agent", "Mozilla")
                    `in` = BufferedInputStream(uc.getInputStream())
                    file = File("temp.mp4")
                    out = FileOutputStream(file)
                    dataBuffer = ByteArray(1024)
                    while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                        out.write(dataBuffer, 0, bytesRead)
                    }
                    if (Arrays.equals(
                                    Files.readAllBytes(
                                            file.toPath()),
                                    javaClass.getResourceAsStream("/Putin_Walk.mp4").readAllBytes())) {
                        msg.delete().reason("Bad meme.").queue()
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        if (content.contains("joke") || content.contains("joking")) {
            for (reaction in notFunny) msg.addReaction(reaction).queue()
        }
    }
}