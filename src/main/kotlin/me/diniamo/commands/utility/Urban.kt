package me.diniamo.commands.utility

import com.beust.klaxon.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.THUMBS_DOWN
import me.diniamo.THUMBS_UP
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import net.dv8tion.jda.api.EmbedBuilder
import okhttp3.Request
import java.net.URLEncoder

class Urban : Command(
    "urban", arrayOf("u"), Category.UTILITY,
    "Searches in Urban Dictionary for a definition", "<search query>"
) {

    override fun run(ctx: CommandContext) {
        if(ctx.args.isEmpty()) {
            reply(ctx, "Usage: ${CommandClient.prefix}$name $arguments", "Urban")
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://api.urbandictionary.com/v0/define?term=${URLEncoder.encode(ctx.args.joinToString(" "), Charsets.UTF_8)}")
                .get().build()

            try {
                val result = Values.httpClient.newCall(request).execute().body?.string()
                if(result == null) {
                    reply(ctx, "No result.", "Urban")
                    return@launch
                }
                val list = (Values.jsonParser.parse(StringBuilder(result)) as JsonObject).array<JsonObject>("list")
                val mostLiked = list?.maxByOrNull {
                    (it.int("thumbs_up") ?: 0) - (it.int("thumbs_down") ?: 0)
                }!!

                reply(ctx, EmbedBuilder()
                    .setTitle("Definition of ${mostLiked.string("word")}", mostLiked.string("permalink"))
                    .setColor(Values.averagePfpColor)
                    .setThumbnail("https://i.imgur.com/VFXr0ID.jpg")
                    .setFooter("Author: ${mostLiked.string("author")}")
                    .appendDescription(mostLiked.string("definition") ?: "No definition.")
                    .addField("Example", mostLiked.string("example"), false)
                    .addField(THUMBS_UP, mostLiked.int("thumbs_up").toString(), true)
                    .addField(THUMBS_DOWN, mostLiked.int("thumbs_down").toString(), true).build())
            } catch(ex: Exception) {
                replyError(ctx, "Something went wrong.", "Urban")
            }
        }
    }
}