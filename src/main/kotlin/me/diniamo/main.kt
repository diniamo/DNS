@file:JvmName("DNS")

package me.diniamo

import ch.jalu.configme.SettingsManager
import ch.jalu.configme.SettingsManagerBuilder
import me.diniamo.commands.*
import me.diniamo.commands.memes.*
import me.diniamo.commands.system.CommandClient
import me.diniamo.events.BinaryToText
import me.diniamo.events.Counting
import me.diniamo.events.SimpleStuff
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
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

    val builder = CommandClient(prefix, 388742599483064321L, jda)
    builder.addCommands(Ping(), Emote(), EvalCommand(), Translate(), Google(), Uptime(), Info(), Color(), EchoCommand())
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    builder.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), Bonk(), HeartBeat())

    jda.addEventListener(BinaryToText(), Counting(), SimpleStuff())
    jda.addEventListener(builder)
}
