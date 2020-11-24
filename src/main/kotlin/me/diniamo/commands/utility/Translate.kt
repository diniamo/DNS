package me.diniamo.commands.utility

import com.beust.klaxon.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Values
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import okhttp3.Request
import java.net.URLEncoder

class Translate : Command(
    "translate", arrayOf(), Category.UTILITY,
    "Uses the google translate API to translate text", "<language to (2 letter form)> <text>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            val args = ctx.args
            val request = Request.Builder()
                .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + args[0] + "&dt=t&q=" +
                        URLEncoder.encode(args.filterIndexed { index, _ -> (index != 0) }.joinToString(separator = " "), Charsets.UTF_8.name()))
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