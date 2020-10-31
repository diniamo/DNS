package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder

class Translate : Command() {
    init {
        name = "translate"
        arguments = "<language from (2 letter form)> <language to (2 letter form)> <text>"
    }

    override fun execute(event: CommandEvent) {
        Utils.scheduler.execute {
            val args = event.args.split(" ")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + args[0] + "&tl=" + args[1] + "&dt=t&q=" +
                    URLEncoder.encode(args.filterIndexed { index, _ -> (index != 0 && index != 1) }.joinToString(separator = " "), Charsets.UTF_8.name())
            try {
                val text = URL(url).readText()
                //println(text)

                val builder = StringBuilder()
                val array = JSONArray(text).getJSONArray(0)
                for (i in 0 until array.length()) {
                    builder.append(array.getJSONArray(i).get(0))
                }

                event.reply(builder.toString())
                //event.reply(text.substring(4, text.indexOf("\",")))
            } catch (ex: Exception) {
                event.reply("Something went wrong.")
            }

        }
    }
}