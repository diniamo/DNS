package me.diniamo.commands.`fun`

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandContext
import java.net.URL

class ThisPersonDoesNotExist : Command(
    "thispersondoesnotexist", arrayOf("tpdne", "fakeperson"), Category.FUN,
    "Creates a non-existant person", ""
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            ctx.channel.sendFile(URL("https://thispersondoesnotexist.com/image").openStream(), "fakeperson.jpg").queue()
        }
    }
}