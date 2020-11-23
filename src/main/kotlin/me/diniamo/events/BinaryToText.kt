package me.diniamo.events

import me.diniamo.Utils
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import javax.annotation.Nonnull

class BinaryToText : ListenerAdapter() {
    override fun onMessageReceived(@Nonnull event: MessageReceivedEvent) {
        val attachments = event.message.attachments
        try {
            if (attachments.isEmpty()) {
                event.channel.sendMessage(toText(event.message.contentStripped)).queue()
            } else {
                for (att in attachments) {
                    event.channel.sendMessage(toText(Files.readString(Utils.downloadFile(att).toPath()))).queue()
                }
            }
        } catch(ex: Exception) {}
    }

    @Throws(Exception::class)
    private fun toText(binary: String): String {
        val s = StringBuilder(" ")
        var index = 0
        while (index < binary.length) {
            val temp = binary.substring(index, index + 8)
            val num = temp.toInt(2)
            val letter = num.toChar()
            s.append(letter)
            index += 9
        }
        return s.toString()
    }
}