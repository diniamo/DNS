package me.diniamo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.diniamo.Utils.Companion.addReactions
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.Integer.max
import java.lang.Integer.min

const val ARROW_LEFT = "\u2B05\uFE0F"
const val ARROW_RIGHT = "\u27A1\uFE0F"

object Paginator : ListenerAdapter() {
    private val menus = mutableListOf<Menu>()

    fun createMenu(title: String, pages: List<Page>, channel: MessageChannel) {
        menus.add(Menu(
            pages, channel.idLong
        ).also { menu ->
            channel.sendMessage(
                EmbedBuilder().setTitle(title).setColor(Values.averagePfpColor)
                    .setDescription(menu.pages[menu.pageNum].text)
                    .setImage(menu.pages[menu.pageNum].image)
                    .setFooter("Page: ${menu.pageNum + 1}/${menu.pages.size}")
                    .build()
            ).queue { m -> menu.messageId = m.idLong; m.addReactions(ARROW_LEFT, ARROW_RIGHT);
                GlobalScope.launch(Dispatchers.Default) {
                    delay(60_000)

                    menus.remove(menu)
                }
            }
        })
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.user?.isBot ?: return) return

        val menu = menus.firstOrNull { it.messageId == event.messageIdLong } ?: return

        if (event.channel.idLong == menu.channelId) {
            try { event.reaction.removeReaction(event.user!!).queue() } catch(ex: InsufficientPermissionException) {} // Ignore if we cant remove the reaction

            val channel: MessageChannel? =
                if (event.isFromGuild) event.jda.getTextChannelById(event.channel.idLong) else event.jda.getPrivateChannelById(
                    event.channel.idLong
                )

            channel?.retrieveMessageById(menu.messageId)!!.queue { m ->
                menu.pageNum = when (event.reactionEmote.emoji) {
                    ARROW_LEFT -> {
                        max(0, menu.pageNum-1)
                    }
                    ARROW_RIGHT -> {
                        min(menu.pages.size - 1, menu.pageNum+1)
                    }
                    else -> return@queue
                }

                EmbedBuilder(m.embeds.first())
                    .setImage(menu.pages[menu.pageNum].image)
                    .setDescription(menu.pages[menu.pageNum].text)
                    .setFooter("Page: ${menu.pageNum + 1}/${menu.pages.size}").build().let { built ->
                        m.editMessage(built).queue()
                    }
            }
        }
    }
}

class Menu(
    val pages: List<Page>, val channelId: Long,
) {
    var messageId: Long = -1;
    var pageNum: Int = 0
}

class Page(
    val text: String, val image: String? = null
)