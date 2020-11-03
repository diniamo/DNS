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
import kotlin.math.exp

class CommandClient(val prefix: String, val ownerId: Long, jda: JDA) : ListenerAdapter() {
    private val commandMap = HashMap<String, MyCommand>()

    fun addCommands(vararg toAdd: MyCommand) {
        for (command in toAdd) {
            commandMap.keys.forEach {
                if (command.name == it || command.aliases.any(it::equals)) throw IllegalStateException(
                    "You have the same alias or name in 2 commands. (${it}-${command.name})"
                )
            }

            commandMap[command.name] = command
            command.aliases.forEach { commandMap[it] = command }
        }
        //println(commandMap.keys.forEach { println("$it     -     ${commandMap[it]}") })
    }

    private val spaces = Regex("\\s+")
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val args = event.message.contentRaw.split(spaces)
        //println(key)
        val expectedCommand = commandMap[args[0].apply { if(startsWith(prefix)) substringAfter(prefix) else return@onMessageReceived }.toLowerCase(Locale.ROOT)]

        if (expectedCommand != null) {
            val errorBuilder = EmbedBuilder().setAuthor("Error", null, event.jda.selfUser.effectiveAvatarUrl).setColor(Color.RED)
                .setFooter(event.member?.effectiveName ?: event.author.name, event.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now())

            if (expectedCommand.ownerCommand) {
                if (ownerId == event.author.idLong) {
                    expectedCommand.execute(CommandContext(event, removeFirst(args)))
                } else {
                    errorBuilder.appendDescription("Missing permission `OWNER`")
                    event.channel.sendMessage(errorBuilder.build()).queue()
                }
                return
            }

            if(expectedCommand.guildOnly && !event.isFromGuild) {
                errorBuilder.appendDescription("This command cannot be used here.")
                event.channel.sendMessage(errorBuilder.build()).queue { msg -> answerCache[event.message.idLong] = msg.idLong }
            }

            if (hasPermission(event.member, expectedCommand.permission)) {
                expectedCommand.execute(CommandContext(event, removeFirst(args)))
            } else {
                errorBuilder.appendDescription("Missing permission `${expectedCommand.permission?.name}`")
                event.channel.sendMessage(errorBuilder.build()).queue { msg -> answerCache[event.message.idLong] = msg.idLong }
                return
            }

            /*if((expectedCommand.guildOnly && event.isFromGuild && hasPermission(event.member, expectedCommand.permission))
                && (expectedCommand.ownerCommand && event.author.idLong == ownerId))
                expectedCommand.execute(CommandContext(event, removeFirst(args)))
            else event.channel.sendMessage(EmbedBuilder()
                .appendDescription("You can't do that!")
                .setColor(Color.RED)
                .setFooter(event.member?.effectiveName ?: event.author.name, event.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()).queue { msg -> answerCache[event.messageIdLong] = msg.idLong }*/
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
        answerCache = AnswerCache(jda)
    }

    companion object {
        lateinit var answerCache: AnswerCache<Long, Long>
    }
}

