package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Message
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

class RemoveFromEarth : Command() {
    private val mainExecutor = Executors.newSingleThreadScheduledExecutor()
    private val subExecutor = Executors.newSingleThreadScheduledExecutor()
    private val msgList = arrayOf("Removing %s from the earth", "Rocket initialized", "Subject placed in the rocket", "Rocket launching in 3...2...1...",
            "Rocket launched", "Goal has been set to planet Mars", "Arrived at planet Mars")

    init {
        name = "removefromearth"
        aliases = arrayOf("rfe")
        cooldown = 15
        cooldownScope = CooldownScope.GUILD
    }

    override fun execute(event: CommandEvent) {
        var i = 0
        lateinit var msg: Message;
        val removing = event.message.mentionedMembers[0];
        val muted = event.guild.getRolesByName("Muted", false)[0]
        event.channel.sendMessage(String.format(msgList[i++], removing)).queue {
            message -> msg = message

            mainExecutor.scheduleAtFixedRate( {
                msg.editMessage(msgList[i++]).queue()
                if(i == 7) {
                    i = 0;
                    removing.modifyNickname("Location: Mars")
                    event.guild.addRoleToMember(removing, muted).queue {
                        subExecutor.schedule({
                            event.guild.removeRoleFromMember(removing, muted)
                            mainExecutor.shutdownNow()
                        }, ThreadLocalRandom.current().nextLong(3, 8), TimeUnit.SECONDS)
                    }
                    event.reply("${removing.asMention} is now too far to talk to us...")
                }
            }, 1, 1, TimeUnit.SECONDS)
        }
    }
}