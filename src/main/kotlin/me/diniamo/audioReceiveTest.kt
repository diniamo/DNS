package me.diniamo

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.audio.UserAudio
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import java.util.concurrent.ConcurrentLinkedQueue
import javax.security.auth.login.LoginException


class AudioBridge : AudioReceiveHandler, AudioSendHandler {
    private var volume = 1.0
    private val bridgeQueue = ConcurrentLinkedQueue<ByteArray>()

    override fun canReceiveCombined(): Boolean {
        return true
    }

    override fun canReceiveUser(): Boolean {
        return false
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        bridgeQueue.add(combinedAudio.getAudioData(volume))
    }

    override fun handleUserAudio(userAudio: UserAudio) {}

    fun handleUserTalking(user: User, talking: Boolean) {}

    override fun canProvide(): Boolean {
        return !bridgeQueue.isEmpty()
    }

    override fun provide20MsAudio(): ByteArray {
        return bridgeQueue.poll()!!
    }
}

class BridgeBotExample : ListenerAdapter() {
    var currentTo: AudioManager? = null
    var currentFrom: AudioManager? = null
    var bridge = AudioBridge()
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        //DV8FromTheWorld is the only one who can control this bot. Change/comment if you want to control!
        if (event.author.id != "107562988810027008") return
        val msg: String = event.message.getContent()

        //Expects command format: .bridge from NAME_OF_CHANNEL
        if (msg.startsWith(".bridge from ")) {
            val chanName = msg.substring(".bridge from ".length)
            val chan: VoiceChannel = getChannelWithName(event.guild, chanName)
            if (chan == null) {
                event.channel.sendMessage("There isn't a VoiceChannel in this Guild with the name: '$chanName'")
                return
            }
            if (currentFrom != null) {
                currentFrom!!.sendingHandler = null
                currentFrom!!.closeAudioConnection()
            }
            currentFrom = event.guild.audioManager
            currentFrom!!.openAudioConnection(chan)
            currentFrom!!.receivingHandler = bridge
        }

        //Expects command format: .bridge to NAME_OF_CHANNEL
        if (msg.startsWith(".bridge to ")) {
            val chanName = msg.substring(".bridge to ".length)
            val chan: VoiceChannel = getChannelWithName(event.guild, chanName)
            if (chan == null) {
                event.channel.sendMessage("There isn't a VoiceChannel in this Guild with the name: '$chanName'")
                return
            }
            if (currentTo != null) {
                currentTo!!.sendingHandler = null
                currentTo!!.closeAudioConnection()
            }
            currentTo = event.guild.audioManager
            currentTo!!.openAudioConnection(chan)
            currentTo!!.sendingHandler = bridge
        }
    }

    fun getChannelWithName(guild: Guild, chanName: String?): VoiceChannel {
        //Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
        return guild.getVoiceChannels().stream().filter { vChan ->
            vChan.getName().equalsIgnoreCase(chanName)
        }
            .findFirst().orElse(null)
    }

    companion object {
        @Throws(LoginException::class, InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val api: JDA = JDABuilder()
                .setBotToken("BOT_LOGIN_TOKEN_HERE")
                .addListener(BridgeBotExample())
                .buildBlocking()
        }
    }
}