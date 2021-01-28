package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandClient
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


class Eval(private val client: CommandClient) : Command(
    "eval", arrayOf(), Category.ADMIN, "Evaluates Groovy code", "<code (without Discord formatting)>", ownerCommand = true
) {
    val engine: ScriptEngine by lazy {
        ScriptEngineManager().getEngineByExtension("kts")!!.apply {
            this.eval("""
        import net.dv8tion.jda.api.*
        import net.dv8tion.jda.api.entities.*
        import net.dv8tion.jda.api.exceptions.*
        import net.dv8tion.jda.api.utils.*
        import net.dv8tion.jda.api.requests.restaction.*
        import net.dv8tion.jda.api.requests.*
        import kotlin.collections.*
        import kotlinx.coroutines.*
        import java.util.*
        import java.util.concurrent.*
        import java.util.stream.*
        import java.io.*
        import java.time.*
        """.trimIndent())
        }
    }
    override fun run(ctx: CommandContext) {
        if (ctx.user.idLong == 388742599483064321L) {
            engine.put("jda", ctx.jda)
            engine.put("api", ctx.jda)
            engine.put("channel", ctx.channel)
            engine.put("guild", ctx.guild)
            engine.put("ctx", ctx)
            engine.put("message", ctx.message)
            engine.put("member", ctx.member)
            engine.put("user", ctx.user)
            engine.put("bot", ctx.jda.selfUser)
            engine.put("client", client)

            val builder = EmbedBuilder().setTitle("Evaluate")
            val code = ctx.message.contentRaw.substringAfter("${CommandClient.prefix}eval")
            val startTime = System.currentTimeMillis()
            try {
                val out = engine.eval(code)

                builder.addField("Status:", "Success", true)
                builder.addField("Duration:", "${System.currentTimeMillis() - startTime}ms", true)
                builder.setColor(Color.GREEN)
                builder.addField("Code:", "```groovy\n$code```", false)
                builder.addField("Result:", out?.toString() ?: "Executed without error.", true)
            } catch (ex: Exception) {
                builder.addField("Status:", "Error", true)
                builder.addField("Duration:", "${System.currentTimeMillis() - startTime}ms", true)
                builder.setColor(Color.RED)
                builder.addField("Code:", "```groovy\n$code```", false)
                builder.addField("Error:", "```$ex```", true)
            }
            reply(ctx, builder.build())
        }
    }
}
