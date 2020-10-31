package me.diniamo.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


class EvalCommand : Command() {
    private var engine: ScriptEngine

    private val DEFAULT_IMPORTS = arrayOf("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
            "java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream")

    init {
        name = "eval"
        aliases = arrayOf("e")
        arguments = "<code>"

        engine = ScriptEngineManager().getEngineByName("groovy")
    }

    override fun execute(event: CommandEvent) {
        if (event.author.idLong == 388742599483064321L) {
            engine.put("jda", event.jda)
            engine.put("api", event.jda)
            engine.put("channel", event.channel)
            engine.put("guild", event.guild)
            engine.put("event", event)
            engine.put("message", event.message)
            engine.put("sender", event.member)
            engine.put("bot", event.jda.selfUser)

            val builder = EmbedBuilder().setTitle("Evaluate")
            val startTime = System.currentTimeMillis()
            try {
                val sb = StringBuilder()
                DEFAULT_IMPORTS.forEach { imp -> sb.append("import ").append(imp).append(".*; ") }
                sb.append("\n" + event.args)
                val out = engine.eval(sb.toString())

                builder.addField("Status:", "Success", true)
                builder.addField("Duration:", "${System.currentTimeMillis() - startTime}ms", true)
                builder.setColor(Color.GREEN)
                builder.addField("Code:", "```groovy\n${event.args}```", false)
                builder.addField("Result:", out?.toString() ?: "Executed without error.", true)
            } catch (ex: Exception) {
                builder.addField("Status:", "Error", true)
                builder.addField("Duration:", "${System.currentTimeMillis() - startTime}ms", true)
                builder.setColor(Color.RED)
                builder.addField("Code:", "```groovy\n${event.args}```", false)
                builder.addField("Error:", "```$ex```", true)
            }
            event.reply(builder.build())
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
