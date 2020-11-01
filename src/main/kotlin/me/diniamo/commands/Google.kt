package me.diniamo.commands

import me.diniamo.Utils
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import org.jsoup.Jsoup
import java.net.URLEncoder


class Google : MyCommand(
    "google", arrayOf("g"), Category.UTILITY,
    "Queries the google API with the given query and sends the first result.", "<query>"
) {
    override fun execute(ctx: CommandContext) {
        Utils.scheduler.execute {
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