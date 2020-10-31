@file:JvmName("DNS")

package me.diniamo

import ch.jalu.configme.SettingsManager
import ch.jalu.configme.SettingsManagerBuilder
import com.jagrosh.jdautilities.command.CommandClientBuilder
import me.diniamo.commands.*
import me.diniamo.commands.memes.*
import me.diniamo.events.AutoRoler
import me.diniamo.events.BinaryToText
import me.diniamo.events.Counting
import me.diniamo.events.SimpleStuff
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.File

val config: SettingsManager = SettingsManagerBuilder
        .withYamlFile(File("./config.yml"))
        .configurationData(Config::class.java)
        .useDefaultMigrationService()
        .create()
val prefix: String = config.getProperty(Config.BOT_PREFIX)

fun main() {
    val jda = JDABuilder.createDefault(config.getProperty(Config.BOT_TOKEN)).enableIntents(GatewayIntent.GUILD_MEMBERS)
            //.setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL)
            .build().awaitReady()

    System.setProperty("http.agent", "")
    Values.ffmpeg = config.getProperty(Config.FFMPEG)

    val builder = CommandClientBuilder()
    builder.setOwnerId("388742599483064321")
    builder.setPrefix(prefix)
    builder.setHelpWord("help")
    builder.addCommands(Ping(), RolesOf(), Emote(), EvalCommand(), Translate(), TimeZones(), Google(), Uptime())
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    builder.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), Bonk(), HeartBeat())

    jda.addEventListener(BinaryToText(), AutoRoler(), Counting(), SimpleStuff())
    jda.addEventListener(builder.build())
}
