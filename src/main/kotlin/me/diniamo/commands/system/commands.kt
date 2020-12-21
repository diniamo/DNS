package me.diniamo.commands.system

import me.diniamo.Values
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.time.Instant

abstract class Command(
    val name: String,
    val aliases: Array<String>,
    val category: Category,
    val help: String? = null,
    val arguments: String? = null,
    val permissions: Set<Permission> = setOf(),
    val guildOnly: Boolean = false,
    val ownerCommand: Boolean = false
) {
    protected fun templateBuilder(ctx: CommandContext) = EmbedBuilder().setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl).setTimestamp(Instant.now())

    protected fun reply(ctx: CommandContext, message: Message) {
        ctx.channel.sendMessage(message).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }

    protected fun reply(ctx: CommandContext, embed: MessageEmbed) {
        ctx.channel.sendMessage(embed).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }

    // Reply with description
    protected fun reply(ctx: CommandContext, text: String, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setColor(Values.averagePfpColor)
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
    protected fun replySuccess(ctx: CommandContext, text: String, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.GREEN)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
    protected fun replyError(ctx: CommandContext, text: String, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.RED)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }

    // Reply with fields
    protected fun reply(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setColor(Values.averagePfpColor)
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
    protected fun replySuccess(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.GREEN)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }
    protected fun replyError(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String?) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setAuthor(title, null, ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.RED)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[ctx.message.idLong] = msg.idLong
        }
    }

    abstract fun run(ctx: CommandContext)
}

data class CommandContext(
    val event: MessageReceivedEvent,
    val args: Array<String>,
    val jda: JDA = event.jda,
    val guild: Guild? = if(event.isFromGuild) event.guild else null,
    val channel: MessageChannel = event.channel,
    val message: Message = event.message,
    val user: User = event.author,
    val member: Member? = event.member,
) {
    constructor(ctx: CommandContext) : this(ctx.event, ctx.args, ctx.jda, ctx.guild, ctx.channel, ctx.message, ctx.user, ctx.member)
}