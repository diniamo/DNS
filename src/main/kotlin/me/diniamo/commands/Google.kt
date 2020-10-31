package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import me.diniamo.Utils
import org.jsoup.Jsoup
import java.net.URLEncoder


class Google : Command() {
    init {
        name = "google"
        aliases = arrayOf("g")
        arguments = "<search query>"
    }

    override fun execute(event: CommandEvent) {
        Utils.scheduler.execute {
            try {
                val doc = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(event.args, Charsets.UTF_8.name())).get()

                event.reply(doc.getElementsByClass("yuRUbf")[0].child(0).attr("abs:href"))
            } catch (ex: Exception) {
                event.reply("An error occurred while executing the command.")
                ex.printStackTrace()
            }
        }
    }
}