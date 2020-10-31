package me.diniamo.commands.system

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class MyCommand(
    val name: String, val aliases: Array<String>, val category: Category, val help: String?, val arguments: String?
) {
    abstract fun execute(ctx: CommandContext)
}

data class CommandContext(
    val event: MessageReceivedEvent,
    val args: Array<String>,
    val jda: JDA = event.jda,
    val message: Message = event.message,
    val user: User = event.author,
    val member: Member? = event.member,
)