package me.diniamo.events

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Counting : ListenerAdapter() {
    var isGoing = true
    var record = 0
    var currentNumber = 0

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.name == "counting") {
            val message = event.message.contentStripped.toLowerCase()
            //            // val permissionMessage: String? = if(event.member?.hasPermission(Permission.MANAGE_SERVER) == true) "You don't have the required permissions.\n" else null

            when (val sub = message.split(" ")[1]) {
                "start" -> {
                    if (event.member?.hasPermission(Permission.MANAGE_SERVER) == true && !isGoing) {
                        isGoing = true; event.channel.sendMessage("0").queue()
                    } else event.channel.sendMessage("You don't have the required permissions or the game is already going.").queue()
                }
                "end" -> {
                    if (event.member?.hasPermission(Permission.MANAGE_SERVER) == true && isGoing) {
                        isGoing = false; currentNumber = 0; event.channel.sendMessage("Game stopped.").queue()
                    } else event.channel.sendMessage("You don't have the required permissions or the game has already been stopped.").queue()
                }
                "restart" -> if (event.member?.hasPermission(Permission.MANAGE_SERVER) == true) restart(event.channel)
                else -> {
                    try {
                        val number = sub.toInt()

                        if (++currentNumber != number) {
                            event.channel.sendMessage("${event.author.asMention} is bad at counting.").queue()
                            restart(event.channel)
                        }
                    } catch (ex: Exception) {
                        event.channel.sendMessage("${event.author.asMention}, you may only send numbers.").queue()
                        restart(event.channel)
                    }
                }
            }
        }
    }

    private fun restart(channel: TextChannel) {
        val isNew = currentNumber > record
        channel.sendMessage(if(isNew) "New record! New: $currentNumber Old: ${record}\n" else "" + "Restarting... Current record: ${if(isNew) record.apply { currentNumber } else record}\n0").queue()
        currentNumber = 0
    }
}


