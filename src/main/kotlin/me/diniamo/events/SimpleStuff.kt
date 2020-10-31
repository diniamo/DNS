package me.diniamo.events

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SimpleStuff : ListenerAdapter() {
    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.retrieveMemberById(356352140861636608L).queue {
            sendSpam(
                    it,
                    event.guild.getTextChannelById(707685614820262003L)!!
            )
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        /*val message = event.message.contentStripped

        if(message.toLowerCase().contains("@someone")) {
            val canPing = event.guild.members.filterNot { it.user.isBot }
            println(canPing)
            event.channel.sendMessage(canPing[ThreadLocalRandom.current().nextInt(canPing.size)].asMention).queue()
        }*/
    }

    private fun sendSpam(member: Member, channel: TextChannel) {
        channel.sendMessage(member.asMention).queue {
            sendSpam(member, channel)
        }
    }
}