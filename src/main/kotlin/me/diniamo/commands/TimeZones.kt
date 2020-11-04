package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import net.dv8tion.jda.api.entities.MessageEmbed
import java.text.SimpleDateFormat
import java.util.*

class TimeZones : Command(
    "timezones", arrayOf("times", "tz"), Category.UTILITY,
) {
    override fun run(ctx: CommandContext) {
        /*val current = Clock.System.now()

        event.channel.sendMessage(
                "Dalv (UTC+3): " + format(current.toLocalDateTime(TimeZone.of("UTC+3"))) +
                        "\ndiniamo, Vajdani (UTC+2): " + format(current.toLocalDateTime(TimeZone.of("UTC+2"))) +
                        "\nCraig, boo, Booby, jj (UTC+1): " + format(current.toLocalDateTime(TimeZone.of("UTC+1")))).queue()*/

        val date = Date()
        val formatter = SimpleDateFormat("HH:mm")

        reply(
            ctx, arrayOf(
                MessageEmbed.Field(
                    "Craig, Booby, jj (UTC+1):",
                    formatter.formatForDalv(TimeZone.getTimeZone("GMT+1"), date),
                    false
                ),
                MessageEmbed.Field(
                    "\ndiniamo, Vajdani (UTC+2):",
                    formatter.formatForDalv(TimeZone.getTimeZone("GMT+2"), date),
                    false
                ),
                MessageEmbed.Field(
                    "\nDalv (UTC+3):",
                    formatter.formatForDalv(TimeZone.getTimeZone("GMT+3"), date),
                    false
                )
            ), "Timezones"
        )
    }

    //private fun format(date: LocalDateTime): String = "${date.hour}:${date.minute}"

    private fun SimpleDateFormat.formatForDalv(zone: TimeZone, date: Date): String {
        timeZone = zone
        applyPattern("HH:mm")
        return format(date) + " | " + apply { applyPattern("hh:mm") }.format(date) +
                if (Calendar.getInstance().apply { timeZone = zone }
                        .get(Calendar.HOUR_OF_DAY) in 12..23) " PM" else " AM"
    }
}