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

class CommandClient(val prefix: String, val ownerId: Long, jda: JDA) : ListenerAdapter() {
    private val commandMap = HashMap<String, MyCommand>()

    fun addCommands(vararg toAdd: MyCommand) {
        for (command in toAdd) {
            toAdd.forEach {
                if (command.aliases.any(it.aliases::contains)) throw IllegalStateException(
                    "You have the same alias or name in 2 commands."
                )
            }

            commandMap[command.name]
            command.aliases.forEach { commandMap[it] }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val args = event.message.contentRaw.split("\\s+")
        val expectedCommand = commandMap[prefix + args[0].toLowerCase()]

        if (expectedCommand != null) {
            if((expectedCommand.guildOnly && event.isFromGuild && hasPermission(event.member, expectedCommand.permission))
                && (expectedCommand.ownerCommand && event.author.idLong == ownerId))
                expectedCommand.execute(CommandContext(event, removeFirst(args)))
            else event.channel.sendMessage(EmbedBuilder()
                .appendDescription("You can't do that!")
                .setColor(Color.RED)
                .setFooter(event.member?.effectiveName ?: event.author.name, event.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now()).build()).queue { msg -> answerCache[event.messageIdLong] = msg.idLong }
        }
    }

    private fun hasPermission(member: Member?, permission: Permission?): Boolean {
        return member != null && (permission == null || member.hasPermission(permission))
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val answer = answerCache[event.messageIdLong]

        if(answer != null) {
            event.channel.deleteMessageById(answer)
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

