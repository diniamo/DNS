package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.text.SimpleDateFormat
import java.util.*

class TimeZones : Command() {
    init {
        name = "timezones"
        aliases = arrayOf("times", "tz")
    }

    override fun execute(event: CommandEvent) {
        /*val current = Clock.System.now()

        event.channel.sendMessage(
                "Dalv (UTC+3): " + format(current.toLocalDateTime(TimeZone.of("UTC+3"))) +
                        "\ndiniamo, Vajdani (UTC+2): " + format(current.toLocalDateTime(TimeZone.of("UTC+2"))) +
                        "\nCraig, boo, Booby, jj (UTC+1): " + format(current.toLocalDateTime(TimeZone.of("UTC+1")))).queue()*/

        val date = Date()
        val formatter = SimpleDateFormat("HH:mm")

        event.reply(
                "Craig, Booby, jj (UTC+1): " + formatter.formatForDalv(TimeZone.getTimeZone("GMT+1"), date) +
                "\ndiniamo, Vajdani (UTC+2): " + formatter.formatForDalv(TimeZone.getTimeZone("GMT+2"), date) +
                "\nDalv (UTC+3): " + formatter.formatForDalv(TimeZone.getTimeZone("GMT+3"), date)
        )
    }

    //private fun format(date: LocalDateTime): String = "${date.hour}:${date.minute}"

    private fun SimpleDateFormat.formatForDalv(zone: TimeZone, date: Date): String {
        timeZone = zone
        applyPattern("HH:mm")
        return format(date) + " | " + apply { applyPattern("hh:mm") }.format(date) +
                if(Calendar.getInstance().apply { timeZone = zone }.get(Calendar.HOUR_OF_DAY) in 12..23) " PM" else " AM"
    }
}