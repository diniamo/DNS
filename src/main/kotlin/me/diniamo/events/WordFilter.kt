package me.diniamo.events

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.regex.Pattern
import javax.annotation.Nonnull

class WordFilter : ListenerAdapter() {
    private val pattern = Pattern.compile("[a-zA-Z0-9]\\*\\?!\\.,")

    override fun onMessageReceived(@Nonnull event: MessageReceivedEvent) {
        val message = event.message.contentRaw.toLowerCase()
                .replace("[., /\\\\]".toRegex(), "")
                .replace("4".toRegex(), "a")
                .replace("0".toRegex(), "o")
                .replace("1".toRegex(), "i")
        val matcher = pattern.matcher(message)
        if (!matcher.matches() || message.contains("didiamo")) event.message.delete().queue()
    }
}