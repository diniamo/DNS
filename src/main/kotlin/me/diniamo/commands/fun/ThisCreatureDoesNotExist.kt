package me.diniamo.commands.`fun`

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandContext
import java.net.URL

class ThisCreatureDoesNotExist : Command(
    "fakecreature", arrayOf("fc"), Category.FUN,
    "Creates a non-existant creature", "<name of the creature>"
) {
    override fun run(ctx: CommandContext) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                ctx.channel.sendFile(
                    URL("https://this${ctx.args[0]}doesnotexist.com/image").openStream(),
                    "fakecreature.jpg"
                ).queue()
            } catch (ex: Exception) {
                replyError(ctx, "There is no support for that yet.", null)
            }
        }
    }
}