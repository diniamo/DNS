package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand

class Emote : MyCommand(
    "emote", arrayOf("emoji", "characterinfo", "charinfo", "ci"), Category.UTILITY,
    "Shows information about characters.", "<character(s)/emote(s)>"
) {
    //private val apiKey = "5ee190e903314b6f329dd8b61d55db75cd2705c7";

    override fun run(ctx: CommandContext) {
        val str = ctx.args.joinToString(" ")

        if(ctx.message.emotes.size > 0) {
            for(emote in ctx.message.emotes) {
                reply(ctx, "Emote **" + emote.name + "**:\n"
                        + "ID: **" + emote.id + "**\n"
                        + "Guild: " + (if (emote.guild == null) "Unknown" else "**" + emote.guild!!.name + "**") + "\n"
                        + "URL: " + emote.imageUrl, "Character Info")
            }

            return
        }

        if (str.codePoints().count() > 15) {
            replyError(ctx, "Invalid emote, or input is too long", "Character Info")
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
        reply(ctx, builder.toString(), "Character Info")
        return
    }
}