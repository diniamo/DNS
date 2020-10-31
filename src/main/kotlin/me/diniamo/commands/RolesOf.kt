package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent

class RolesOf : Command() {
    init {
        name = "rolesof"
    }

    override fun execute(event: CommandEvent) {
        val builder = StringBuilder()
        for(member in event.message.mentionedMembers) {
            for(role in member.roles) {
                builder.append(role.id + ", ")
            }
        }
        event.reply(builder.toString())
    }
}