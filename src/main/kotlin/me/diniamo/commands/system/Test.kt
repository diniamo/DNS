package me.diniamo.commands.system

import me.diniamo.Page
import me.diniamo.Paginator

class Test : Command(
    "test", arrayOf(), Category.ADMIN,
    "The command I use to test stuff.", ownerCommand = true
) {
    override fun run(ctx: CommandContext) {
        Paginator.createMenu("Test menu", listOf(
            Page("This is the first page of the test menu", "https://i1.wp.com/gatherforbread.com/wp-content/uploads/2015/08/Easiest-Yeast-Bread.jpg?resize=500%2C500&ssl=1"),
            Page("This is the second page of the test menu", "https://i2.wp.com/ceklog.kindel.com/wp-content/uploads/2013/02/firefox_2018-07-10_07-50-11.png"),
            Page("This is the third page of the test menu", "https://cdn.discordapp.com/avatars/394607709741252621/5feac405eebf1d98e0b6fabe3c8821b4.png?size=1024"),
        ), ctx.channel, ctx.user.idLong)
    }
}