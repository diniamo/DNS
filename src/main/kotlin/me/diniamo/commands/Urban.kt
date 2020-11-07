package me.diniamo.commands

import me.diniamo.THUMBS_DOWN
import me.diniamo.THUMBS_UP
import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.CommandContext
import net.dv8tion.jda.api.EmbedBuilder
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
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

        Utils.scheduler.execute {
            val request = Request.Builder()
                .url("http://api.urbandictionary.com/v0/define?term=${URLEncoder.encode(ctx.args.joinToString(" "), Charsets.UTF_8)}")
                .get().build()

            try {
                val json = JSONObject(Values.httpClient.newCall(request).execute().body?.string() ?: "{\"list\":[]}")
                val mostLiked = json.getJSONArray("list").maxByOrNull {
                    it as JSONObject
                    it.getInt("thumbs_up") - it.getInt("thumbs_down")
                } as JSONObject

                reply(ctx, EmbedBuilder()
                    .setTitle("Definition of ${mostLiked.getString("word")}", mostLiked.getString("permalink"))
                    .setColor(Values.avaragePfpColor)
                    .setThumbnail("https://i.imgur.com/VFXr0ID.jpg")
                    .setFooter("Author: ${mostLiked.getString("author")}")
                    .appendDescription(mostLiked.getString("definition"))
                    .addField("Example", mostLiked.getString("example"), false)
                    .addField(THUMBS_UP, mostLiked.getInt("thumbs_up").toString(), true)
                    .addField(THUMBS_DOWN, mostLiked.getInt("thumbs_down").toString(), true).build())
            } catch(ex: Exception) {
                replyError(ctx, "Something went wrong.", "Urban")
            }
        }
    }
}