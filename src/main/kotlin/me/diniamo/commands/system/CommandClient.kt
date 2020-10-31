package me.diniamo.commands.system

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandClient(val prefix: String, val ownerId: Long) : ListenerAdapter() {
    private val commandMap = HashMap<String, MyCommand>()

    fun addCommands(vararg toAdd: MyCommand) {
        for(command in toAdd) {
            toAdd.forEach { if(command.aliases.any(it.aliases::contains)) throw IllegalStateException(
                "You have the same alias or name in 2 commands."
            ) }

            commandMap[command.name]
            command.aliases.forEach { commandMap[it] }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val args = event.message.contentRaw.split("\\s+")
        val expectedCommand = commandMap[prefix + args[0].toLowerCase()]

        expectedCommand?.execute(CommandContext(event, removeFirst(args)))
    }

    private fun removeFirst(original: List<String>) = Array(original.size - 1) { i ->
        original[i + 1]
    }
}