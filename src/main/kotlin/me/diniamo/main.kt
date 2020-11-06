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

    val client = CommandClient(prefix, 388742599483064321L, jda)
    client.addCommands(HelpCommand(client), Ping(), Emote(), EvalCommand(), Translate(), Google(), Uptime(), Info(), Color(), EchoCommand())
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    client.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), Bonk(), HeartBeat())

    jda.addEventListener(BinaryToText(), Counting(), SimpleStuff())
    jda.addEventListener(client)

    jda.openPrivateChannelById(388742599483064321L).flatMap { it.sendMessage("The bot has been started") }.queue()
}
