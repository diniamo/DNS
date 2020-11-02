package me.diniamo.commands

import me.diniamo.GREEN_TICK
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.entities.VoiceChannel
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class EchoCommand : MyCommand(
    "echo", arrayOf(), Category.FUN,
    "Voice echo.", "<name or id of the voice channel you want it in (optional it will use the one you are currently in)>",
    ownerCommand = true, guildOnly = true
) {
    val numberRegex = Regex("\\d+")

    override fun execute(ctx: CommandContext) {
        val args = ctx.args

        when(args[0]) {
            "join" -> {
                if(args.size == 1) {
                    val voiceState = ctx.member!!.voiceState
                    val channel = voiceState?.channel

                    if(channel != null) {
                        connectTo(channel)
                        ctx.message.addReaction(GREEN_TICK).queue()
                    } else {
                        replyError(ctx, "You must be in a channel to use this command!", "Echo")
                    }
                } else {
                    var channel: VoiceChannel? = null

                    if(args[1].matches(numberRegex)) {
                        channel = ctx.guild.getVoiceChannelById(args[1].toLong())
                    }
                    if(channel == null) {
                        val channels = ctx.guild.getVoiceChannelsByName(args[1], true)
                        if(channels.isNotEmpty()) channel = channels[0]
                    }

                    if(channel == null) {
                        replyError(ctx, "No channel found.", "Echo")
                        return
                    }

                    connectTo(channel)
                    ctx.message.addReaction(GREEN_TICK).queue()
                }
            }
            "leave" -> {
                val audioManager = ctx.guild.audioManager
                audioManager.sendingHandler = null
                audioManager.receivingHandler = null
                audioManager.closeAudioConnection()

                ctx.message.addReaction(GREEN_TICK).queue()
            }
            else -> replyError(ctx, "Available subcommands: `join`, `leave`", "Echo")
        }


    }

    private fun connectTo(channel: VoiceChannel) {
        val audioManager = channel.guild.audioManager
        val handler = EchoHandler()

        audioManager.sendingHandler = handler
        audioManager.receivingHandler = handler
        audioManager.openAudioConnection(channel)
    }
}

class EchoHandler : AudioSendHandler, AudioReceiveHandler {
    /*
            All methods in this class are called by JDA threads when resources are available/ready for processing.
            The receiver will be provided with the latest 20ms of PCM stereo audio
            Note you can receive even while setting yourself to deafened!
            The sender will provide 20ms of PCM stereo audio (pass-through) once requested by JDA
            When audio is provided JDA will automatically set the bot to speaking!
         */
    private val queue: Queue<ByteArray> = ConcurrentLinkedQueue()

    /* Receive Handling */
    // combine multiple user audio-streams into a single one
    override fun canReceiveCombined(): Boolean {
        // limit queue to 10 entries, if that is exceeded we can not receive more until the send system catches up
        return queue.size < 10
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        // we only want to send data when a user actually sent something, otherwise we would just send silence
        if (combinedAudio.users.isEmpty()) return
        val data = combinedAudio.getAudioData(1.0) // volume at 100% = 1.0 (50% = 0.5 / 55% = 0.55)
        queue.add(data)
    }

    /*
        Disable per-user audio since we want to echo the entire channel and not specific users.
        @Override // give audio separately for each user that is speaking
        public boolean canReceiveUser()
        {
            // this is not useful if we want to echo the audio of the voice channel, thus disabled for this purpose
            return false;
        }
        @Override
        public void handleUserAudio(UserAudio userAudio) {} // per-user is not helpful in an echo system
*/
    /* Send Handling */
    override fun canProvide(): Boolean {
        // If we have something in our buffer we can provide it to the send system
        return !queue.isEmpty()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        // use what we have in our buffer to send audio as PCM
        val data = queue.poll()
        return if (data == null) null else ByteBuffer.wrap(data) // Wrap this in a java.nio.ByteBuffer
    }

    override fun isOpus(): Boolean {
        // since we send audio that is received from discord we don't have opus but PCM
        return false
    }
}