package me.diniamo.commands

import com.beust.klaxon.JsonArray
import me.diniamo.Utils
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import okhttp3.Request
import java.net.URLEncoder

class Translate : Command(
    "translate", arrayOf(), Category.UTILITY,
    "Uses the google translate API to translate text", "<language from (2 letter form)> <language to (2 letter form)> <text>"
) {
    override fun run(ctx: CommandContext) {
        Utils.scheduler.execute {
            val args = ctx.args
            val request = Request.Builder()
                .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + args[0] + "&tl=" + args[1] + "&dt=t&q=" +
                        URLEncoder.encode(args.filterIndexed { index, _ -> (index != 0 && index != 1) }.joinToString(separator = " "), Charsets.UTF_8.name()))
                .build()

            try {
                val response = Values.httpClient.newCall(request).execute()

                val builder = StringBuilder()
                val array = (Values.jsonParser.parse(StringBuilder(response.body?.string() ?: "[]")) as JsonArray<*>)[0] as JsonArray<*>
                for (i in 0 until array.size) {
                    builder.append((array[0] as JsonArray<*>)[0])
                }

                reply(ctx, builder.toString(), "Translate")
                //event.reply(text.substring(4, text.indexOf("\",")))
            } catch (ex: Exception) {
                ex.printStackTrace()
                replyError(ctx, "Something went wrong.", "Translate")
            }
        }
    }
}