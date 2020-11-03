package me.diniamo.commands

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder

class Translate : MyCommand(
    "translate", arrayOf(), Category.UTILITY,
    "Uses the google translate API to translate text.", "<language from (2 letter form)> <language to (2 letter form)> <text>"
) {
    override fun run(ctx: CommandContext) {
        Utils.scheduler.execute {
            val args = ctx.args
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + args[0] + "&tl=" + args[1] + "&dt=t&q=" +
                    URLEncoder.encode(args.filterIndexed { index, _ -> (index != 0 && index != 1) }.joinToString(separator = " "), Charsets.UTF_8.name())
            try {
                val text = URL(url).readText()
                println(text)

                val builder = StringBuilder()
                val array = JSONArray(text).getJSONArray(0)
                for (i in 0 until array.length()) {
                    builder.append(array.getJSONArray(i).get(0))
                }

                reply(ctx, builder.toString(), "Translate")
                //event.reply(text.substring(4, text.indexOf("\",")))
            } catch (ex: Exception) {
                replyError(ctx, "Something went wrong.", "Translate")
            }
        }
    }
}