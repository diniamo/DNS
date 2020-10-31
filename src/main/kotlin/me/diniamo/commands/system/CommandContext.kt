package me.diniamo.commands.system

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class CommandContext(
        val event: MessageReceivedEvent,
        val jda: JDA = event.jda,
        val message: Message = event.message,
        val user: User = event.author,
        val member: Member? = event.member,
)