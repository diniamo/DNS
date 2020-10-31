package me.diniamo.commands.system

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color
import java.time.Instant

abstract class MyCommand(
    val name: String,
    val aliases: Array<String>,
    val category: Category,
    val help: String? = null,
    val arguments: String? = null,
    val guildOnly: Boolean = false,
    val ownerCommand: Boolean = false
) {
    protected fun reply(ctx: CommandContext, embed: MessageEmbed) {
        ctx.channel.sendMessage(embed).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }

    // Reply with description
    protected fun reply(ctx: CommandContext, text: String, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }
    protected fun replySuccess(ctx: CommandContext, text: String, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.GREEN)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }
    protected fun replyError(ctx: CommandContext, text: String, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().appendDescription(text)
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.RED)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }

    // Reply with fields
    protected fun reply(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }
    protected fun replySuccess(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.GREEN)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }
    protected fun replyError(ctx: CommandContext, content: Array<MessageEmbed.Field>, title: String) {
        ctx.channel.sendMessage(
            EmbedBuilder().apply { content.forEach { addField(it) } }
                .setTitle(title)
                .setThumbnail(ctx.jda.selfUser.effectiveAvatarUrl)
                .setColor(Color.RED)
                .setFooter(ctx.member?.effectiveName ?: ctx.user.name, ctx.user.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()
        ).queue {
                msg -> CommandClient.answerCache[msg.idLong] = ctx.message.idLong
        }
    }

    abstract fun execute(ctx: CommandContext)
}

data class CommandContext(
    val event: MessageReceivedEvent,
    val args: Array<String>,
    val jda: JDA = event.jda,
    val channel: MessageChannel = event.channel,
    val message: Message = event.message,
    val user: User = event.author,
    val member: Member? = event.member,
)