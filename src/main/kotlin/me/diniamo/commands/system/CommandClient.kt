package me.diniamo.commands.system

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandClient(val prefix: String, val ownerId: Long, jda: JDA) : ListenerAdapter() {
    private val commandMap = HashMap<String, MyCommand>()

    val answerCache = AnswerCache<Long, Long>(jda) /*Caffeine.newBuilder()
        .maximumSize(jda.guildCache.size() * 10)
        .build<Long, Long>()*/

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
            if((expectedCommand.guildOnly && event.isFromGuild) && (expectedCommand.ownerCommand && event.author.idLong == ownerId))
                expectedCommand.execute(CommandContext(event, removeFirst(args)))
            else event.channel.sendMessage("You can't do that!").queue()
        }
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
}

