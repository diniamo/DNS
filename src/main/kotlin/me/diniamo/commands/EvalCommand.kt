package me.diniamo.commands

import me.diniamo.commands.system.Category
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.MyCommand
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


class EvalCommand : MyCommand(
    "eval", arrayOf("e"), Category.ADMIN, "Evaluates Groovy code.", "<code (without Discord formatting)>", ownerCommand = true
) {
    private val engine: ScriptEngine = ScriptEngineManager().getEngineByName("groovy")

    private val DEFAULT_IMPORTS = arrayOf("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
            "java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream")

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

            val builder = EmbedBuilder().setTitle("Evaluate")
            val code = ctx.args.joinToString(" ")
            val startTime = System.currentTimeMillis()
            try {
                val sb = StringBuilder()
                DEFAULT_IMPORTS.forEach { imp -> sb.append("import ").append(imp).append(".*; ") }
                sb.append("\n" + code)
                val out = engine.eval(sb.toString())

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
            /*eval(engine, event.args)?.let {
                event.reply(it)
            }*/
        }
    }

    /*private fun eval(engine: ScriptEngine, code: String): MessageEmbed? {
        val builder = EmbedBuilder()
                .setColor(Color.decode("#af131a")).setTitle("Evaluate")
                .appendDescription("This is the result of your code:")

        try {
            val out = engine.eval(code)
            if (out != null) {
                builder.appendDescription("```$out```")
            } else return null
        } catch (ex: Exception) {
            builder.appendDescription("```${ex}```")
        }
        return builder.build()
    }*/
}
