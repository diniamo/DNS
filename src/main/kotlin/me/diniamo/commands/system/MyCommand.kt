package me.diniamo.commands.system

abstract class MyCommand(
        val name: String, val category: Category, val aliases: Array<String>?, val help: String?, val arguments: String?
) {

    abstract fun execute(ctx: CommandContext)
}

