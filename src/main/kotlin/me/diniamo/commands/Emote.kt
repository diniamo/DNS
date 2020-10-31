package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Message
import java.util.regex.Matcher
import java.util.regex.Pattern

class Emote : Command() {
    //private val apiKey = "5ee190e903314b6f329dd8b61d55db75cd2705c7";

    //private val checkPattern = Pattern.compile("<a:.*:\\d+>")
    //private val replacePattern = Pattern.compile("<a:.*:(\\d+)>")
    //private val matcher = Matcher()

    init {
        name = "emote"
        aliases = arrayOf("emoji", "characterinfo", "ci")
        help = "Shows information about characters."
    }

    override fun execute(event: CommandEvent) {
        val str = event.args

        if(event.message.emotes.size > 0) {
            for(emote in event.message.emotes) {
                event.reply("Emote **" + emote.name + "**:\n"
                        + "ID: **" + emote.id + "**\n"
                        + "Guild: " + (if (emote.guild == null) "Unknown" else "**" + emote.guild!!.name + "**") + "\n"
                        + "URL: " + emote.imageUrl)
            }

            return
        }
        /*if (matcher.find()) {
            val id = matcher.group(2)
            val emote: Emote? = event.jda.emoteőopeő
            if (emote == null) {
                event.reply("Unknown emote:\n"
                        + "ID: **" + id + "**\n"
                        + "Guild: Unknown\n"
                        + "URL: https://discordcdn.com/emojis/" + id + ".png")
                return
            }
            event.reply("Emote **" + emote.name + "**:\n"
                    + "ID: **" + emote.id + "**\n"
                    + "Guild: " + (if (emote.guild == null) "Unknown" else "**" + emote.guild!!.name + "**") + "\n"
                    + "URL: " + emote.imageUrl)
            return
        }*/
        if (str.codePoints().count() > 15) {
            event.reply("Invalid emote, or input is too long")
            return
        }
        val builder: StringBuilder = StringBuilder("Emoji/Character info:")
        str.codePoints().forEachOrdered { code: Int ->
            val chars = Character.toChars(code)
            var hex = Integer.toHexString(code).toUpperCase()
            while (hex.length < 4) hex = "0$hex"
            builder.append("\n`\\u").append(hex).append("`   ")
            if (chars.size > 1) {
                var hex0 = Integer.toHexString(chars[0].toInt()).toUpperCase()
                var hex1 = Integer.toHexString(chars[1].toInt()).toUpperCase()
                while (hex0.length < 4) hex0 = "0$hex0"
                while (hex1.length < 4) hex1 = "0$hex1"
                builder.append("[`\\u").append(hex0).append("\\u").append(hex1).append("`]   ")
            }
            builder.append(String(chars)).append("   _").append(Character.getName(code)).append("_")
        }
        event.reply(builder.toString())
        return
    }
}