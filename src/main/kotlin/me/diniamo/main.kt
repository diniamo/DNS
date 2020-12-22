@file:JvmName("DNS")

package me.diniamo

import me.diniamo.commands.*
import me.diniamo.commands.`fun`.ThisCatDoesNotExist
import me.diniamo.commands.`fun`.ThisPersonDoesNotExist
import me.diniamo.commands.audio.EchoCommand
import me.diniamo.commands.info.Help
import me.diniamo.commands.info.Info
import me.diniamo.commands.info.Ping
import me.diniamo.commands.info.Uptime
import me.diniamo.commands.memes.*
import me.diniamo.commands.system.CommandClient
import me.diniamo.commands.system.Test
import me.diniamo.commands.utility.*
import me.diniamo.events.BinaryToText
import net.dv8tion.jda.api.GatewayEncoding
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FileInputStream
import java.util.*

val properties = Properties().apply {
    load(FileInputStream("./config.properties"))
}

fun main() {
    val jda = JDABuilder.create(properties.getProperty("token"),
        GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_REACTIONS)
        .setActivity(Activity.playing("with genetics"))
        .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS, CacheFlag.ACTIVITY)
        .setHttpClient(Values.httpClient)
        .setGatewayEncoding(GatewayEncoding.ETF)
        .setMemberCachePolicy(MemberCachePolicy.VOICE)
            .build().awaitReady()

    RestAction.setDefaultFailure {
        println("RestAction failure:")
        it.printStackTrace()
    }

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

    val client = CommandClient(properties.getProperty("prefix"), 388742599483064321L, jda)
    client.addCommands(Help(client), Ping(), Emote(), Eval(client), Translate(), Google(), Uptime(), Info(), Color(), EchoCommand(), Urban(), Test(), ThisPersonDoesNotExist(), ThisCatDoesNotExist(),
        Tag(properties, jda))
            //Tag(jda, config.getProperty(Config.DB_LINK), config.getProperty(Config.DB_DRIVER)))
    client.addCommands(PutinWalk(), AlwaysHasBeen(), ThisIsWhyIsHateVideoGames(), EW(), Distract(), HandWithGun(), HeartBeat(), MacroImage(), WideFish(), Bonk(), Polka())

    jda.addEventListener(BinaryToText(), client, Paginator)
}
