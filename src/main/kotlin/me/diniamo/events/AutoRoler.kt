package me.diniamo.events

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AutoRoler : ListenerAdapter() {
    private val roles = hashMapOf(
            388742599483064321 to arrayOf(738792297055125584, 712607308974391357, 707688806194741321, 737274674198872084, 707688763756642444, 715211670368026687))

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val id = event.user.idLong;

        roles[id]?.forEach {
            event.guild.addRoleToMember(id, event.guild.getRoleById(it)!!).queue()
        }
    }
}