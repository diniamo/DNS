@file:JvmName("DNS")

package me.diniamo

import me.diniamo.commands.*
import me.diniamo.commands.info.Help
import me.diniamo.commands.info.Info
import me.diniamo.commands.info.Ping
import me.diniamo.commands.info.Uptime
import me.diniamo.commands.memes.*
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.Test
import me.diniamo.commands.utility.*
import me.diniamo.events.BinaryToText
import me.diniamo.events.Counting
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FileInputStream
import java.util.*

val properties = Properties().apply {
    load(FileInputStream("./config.properties"))
}

fun main() {
    val jda = JDABuilder.createDefault(properties.getProperty("bot.token")).setActivity(Activity.playing("with genetics"))
            .build().awaitReady()


    System.setProperty("http.agent", "")
    Values.ffmpeg = properties.getProperty("ffmpeg")
    Values.answerCacheSizePerGuild = properties.getProperty("max-cache-size-per-guild").toInt()

    // Font registration
    Font.createFont(Font.TRUETYPE_FONT, File("./arial.ttf")).let { arial ->
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(arial)
    }
    Font.createFont(Font.TRUETYPE_FONT, File("./impact.ttf")).let { impact ->
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(impact)
    }

    val client = CommandClient(properties.getProperty("bot.prefix"), 388742599483064321L, jda)
    client.addCommands(Help(client), Ping(), Emote(), Eval(client), Translate(), Google(), Uptime(), Info(), Color(), EchoCommand(), Urban(), Test())
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    client.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), HeartBeat(), MacroImage(), WideFish(), Bonk(), Polka())

    jda.addEventListener(BinaryToText(), Counting())
    jda.addEventListener(client)
}
