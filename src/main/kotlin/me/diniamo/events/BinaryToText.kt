package me.diniamo.events

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
                var uc: URLConnection
                var input: BufferedInputStream
                var file: File
                var out: FileOutputStream
                var dataBuffer: ByteArray
                var bytesRead: Int
                for (att in attachments) {
                    if (att.fileExtension == "string") {
                        uc = URL(att.url).openConnection()
                        uc.addRequestProperty("User-Agent", "Mozilla")
                        input = BufferedInputStream(uc.getInputStream())
                        file = File("temp.txt")
                        out = FileOutputStream(file)
                        dataBuffer = ByteArray(1024)
                        while (input.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                            out.write(dataBuffer, 0, bytesRead)
                        }
                        event.channel.sendMessage(toText(Files.readString(file.toPath()))).queue()
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    //@Throws(Exception::class)
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