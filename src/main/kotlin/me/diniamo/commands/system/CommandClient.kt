package me.diniamo.commands.system

import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandClient : ListenerAdapter() {
    private val commandMap: HashMap<String, MyCommand> = HashMap()

    fun addCommands(vararg commands: MyCommand) {
        for(command in commands) {
            commandMap[command.name] = command
        }
    }
}