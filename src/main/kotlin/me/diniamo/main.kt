@file:JvmName("DNS")

package me.diniamo

import ch.jalu.configme.SettingsManager
import ch.jalu.configme.SettingsManagerBuilder
import me.diniamo.commands.*
import me.diniamo.commands.memes.*
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.TestCommand
import me.diniamo.events.BinaryToText
import me.diniamo.events.Counting
import me.diniamo.events.SimpleStuff
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File

val config: SettingsManager = SettingsManagerBuilder
        .withYamlFile(File("./config.yml"))
        .configurationData(Config::class.java)
        .useDefaultMigrationService()
        .create()
val prefix: String = config.getProperty(Config.BOT_PREFIX)

fun main() {
    val jda = JDABuilder.createDefault(config.getProperty(Config.BOT_TOKEN)).setActivity(Activity.playing("with genetics"))
            //.setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL)
            .build().awaitReady()


    System.setProperty("http.agent", "")
    Values.ffmpeg = config.getProperty(Config.FFMPEG)
    Values.answerCacheSizePerGuild = config.getProperty(Config.MAX_CACHE_SIZE_PER_GUILD)

    // Font registration
    Font.createFont(Font.TRUETYPE_FONT, File("./arial.ttf")).let { arial ->
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(arial)
    }
    Font.createFont(Font.TRUETYPE_FONT, File("./impact.ttf")).let { impact ->
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(impact)
    }

    val client = CommandClient(prefix, 388742599483064321L, jda)
    client.addCommands(HelpCommand(client), Ping(), Emote(), EvalCommand(), Translate(), Google(), Uptime(), Info(), Color(), EchoCommand(), Urban(), TestCommand())
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    client.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), HeartBeat(), MacroImage(), WideFish(), Bonk())

    jda.addEventListener(BinaryToText(), Counting(), SimpleStuff())
    jda.addEventListener(client)
}
