package me.diniamo.events

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class RepeatingWords : ListenerAdapter() {
    private var users: MutableMap<User, Int> = HashMap()
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        /*if (!event.author.isBot) {
            event.guild.selfMember.modifyNickname(event.member!!.effectiveName)
                    .flatMap { event.channel.sendMessage(event.message).tts(true).flatMap { msg: Message -> msg.delete() } }
                    .flatMap { event.guild.selfMember.modifyNickname("Boi") }.queue()
        }*/

        val message = event.message.contentRaw
        if (message.equals("hello", ignoreCase = true)) {
            if (!users.containsKey(event.author)) {
                users[event.author] = 1
                event.channel.sendMessage("Hi. :)").queue()
            } else {
                when (users[event.author]) {
                    1 -> {
                        event.channel.sendMessage("Hi.").queue()
                        users.replace(event.author, 2)
                    }
                    2 -> {
                        event.channel.sendMessage("Hi!").queue()
                        users.replace(event.author, 3)
                    }
                    3 -> {
                        event.channel.sendMessage("Hello!").queue()
                        users.replace(event.author, 4)
                    }
                    4 -> {
                        event.channel.sendMessage("Hi, you bitch.").queue()
                        users.replace(event.author, 5)
                    }
                    5 -> {
                        event.channel.sendMessage("Hello, you fucking idiot!").queue()
                        users.replace(event.author, 6)
                    }
                    6 -> {
                        event.channel.sendMessage("OK STOP U MORON, DO U THINK IM STUPID?!").queue()
                        users.replace(event.author, 7)
                    }
                    else -> {
                        //event.member!!.kick().reason("FUCK OFF! \uD83E\uDD2C").queue()
                        event.channel.sendMessage("okbye").queue()
                        users.remove(event.author)
                    }
                }
            }
        }
    }
}