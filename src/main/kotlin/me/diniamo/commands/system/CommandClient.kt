package me.diniamo.commands.system

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

class CommandClient(prefix: String, private val ownerId: Long, jda: JDA) : ListenerAdapter() {
    val commandMap = HashMap<String, Command>()

    fun addCommands(vararg toAdd: Command) {
        for (command in toAdd) {
            commandMap.keys.forEach {
                if (command.name == it || command.aliases.any(it::equals)) throw IllegalStateException(
                    "You have the same alias or name in 2 commands. (${it}-${command.name})"
                )
            }

            commandMap[command.name] = command
            command.aliases.forEach { commandMap[it] = command }
        }
    }

    private val spaces = Regex("\\s+")
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(event.message.mentionedUsers.contains(event.jda.selfUser)) {
            event.channel.sendMessage(EmbedBuilder().appendDescription("My prefix is: **$prefix**").build()).queue()
            return
        }

        val args = event.message.contentRaw.split(spaces)
        val expectedCommand = commandMap[if(args[0].startsWith(prefix)) args[0].substringAfter(prefix).toLowerCase(Locale.ROOT) else return]

        if (expectedCommand != null) {
            val errorBuilder = EmbedBuilder().setAuthor("Error", null, event.jda.selfUser.effectiveAvatarUrl).setColor(Color.RED)
                .setFooter(event.member?.effectiveName ?: event.author.name, event.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now())

            if (expectedCommand.ownerCommand) {
                if (ownerId == event.author.idLong) {
                    expectedCommand.run(CommandContext(event, removeFirst(args)))
                } else {
                    errorBuilder.appendDescription("Missing permission `BOT_OWNER`")
                    event.channel.sendMessage(errorBuilder.build()).queue()
                }
                return
            }

            if(expectedCommand.guildOnly && !event.isFromGuild) {
                errorBuilder.appendDescription("This command cannot be used here.")
                event.channel.sendMessage(errorBuilder.build()).queue { msg -> answerCache[event.message.idLong] = msg.idLong }
            }

            if (hasPermission(event.member, expectedCommand.permission)) {
                expectedCommand.run(CommandContext(event, removeFirst(args)))
            } else {
                errorBuilder.appendDescription("Missing permission `${expectedCommand.permission?.name}`")
                event.channel.sendMessage(errorBuilder.build()).queue { msg -> answerCache[event.message.idLong] = msg.idLong }
                return
            }
        }
    }

    private fun hasPermission(member: Member?, permission: Permission?): Boolean {
        return member != null && (permission == null || member.hasPermission(permission))
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val answer = answerCache[event.messageIdLong]

        if (answer != null) {
            event.channel.deleteMessageById(answer).queue()
            answerCache.remove(event.messageIdLong)
        }
    }

    private fun removeFirst(original: List<String>) = Array(original.size - 1) { i ->
        original[i + 1]
    }

    init {
        Companion.prefix = prefix
        answerCache = AnswerCache(jda)
    }

    companion object {
        lateinit var prefix: String
        lateinit var answerCache: AnswerCache<Long, Long>
    }
}

