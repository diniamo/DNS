package me.diniamo.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import org.jsoup.Jsoup
import java.net.URLEncoder


class Google : Command(
    "google", arrayOf("g"), Category.UTILITY,
    "Queries the google API with the given query and sends the first result", "<query>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(ctx.args.joinToString(" "), Charsets.UTF_8.name())).get()

                reply(ctx, doc.getElementsByClass("yuRUbf")[0].child(0).attr("abs:href"), "Google")
            } catch (ex: Exception) {
                replyError(ctx, "An error occurred while executing the command.", "Google")
                ex.printStackTrace()
            }
        }
    }
}