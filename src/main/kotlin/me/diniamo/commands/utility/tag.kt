package me.diniamo.commands.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.diniamo.Page
import me.diniamo.Paginator
import me.diniamo.Utils
import me.diniamo.commands.system.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import org.ktorm.support.postgresql.TextArray
import org.ktorm.support.postgresql.textArray
import java.sql.*
import java.time.OffsetDateTime
import java.util.*

object TagTable : Table<Nothing>("tags") {
    val name = varchar("name")
    val guildId = long("guild_id")
    val authorId = long("author_id")
    val attachments = textArray("attachments")
    val content = varchar("content")
}

private class TagListener(private val database: Database) : ListenerAdapter() {
    override fun onGuildLeave(event: GuildLeaveEvent) {
        GlobalScope.launch(Dispatchers.IO) { deleteTagsFromGuild(event.guild.idLong) }
    }

    override fun onUnavailableGuildLeave(event: UnavailableGuildLeaveEvent) {
        GlobalScope.launch(Dispatchers.IO) { deleteTagsFromGuild(event.guildIdLong) }
    }

    private fun deleteTagsFromGuild(guildId: Long) {
        database.delete(TagTable) {
            it.guildId eq guildId
        }
    }
}

class Tag(private val database: Database, jda: JDA) : Command(
    "tag", arrayOf("t"), Category.UTILITY,
    "Tag system", "<sucommand> <additional arguments>",
    guildOnly = true
) {
    init {
        jda.addEventListener(TagListener(database))
    }

    val subCommands = mutableMapOf<String, Command>(
        "create" to Create(database, this),
        "remove" to Remove(database),
        "list" to List(database),
        "owner" to Owner(database),
        "edit" to Edit(database),
        "search" to Search(database)
    )

    init {
        val tempMap = HashMap(subCommands)
        tempMap.forEach { (k, v) -> subCommands[k[0].toString()] = v }
    }

    override fun run(ctx: CommandContext) {
        require(ctx.guild != null) { GUILD_NULL }

        if (ctx.args.isEmpty()) {
            reply(
                ctx, templateBuilder(ctx)
                    .setTitle("Tag")
                    .appendDescription("With this command you can create tags (local), with a text value. Later on on you can get the text value by their name.")
                    .apply {
                        appendDescription("\n\nSubcommands:\n")
                        subCommands.values.toSet().forEach { v ->
                            appendDescription("- **${v.name}** (${v.help}): ${v.arguments}\n")
                        }
                    }.build()
            )
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val expectedCommand = subCommands[ctx.args[0]]

                if (expectedCommand == null) {
                    getTag(ctx.guild.idLong, ctx.args[0])?.let {
                        ctx.channel.sendMessage(it.content + "\n\n" + it.attachments.joinToString("\n")).queue { msg ->
                            CommandClient.answerCache[ctx.message.idLong] =
                                MessageData(msg.idLong, OffsetDateTime.now())
                        }
                    } ?: replyError(ctx, "There is no such tag in this guild.", "Tag")
                } else {
                    // remove first argument
                    expectedCommand.run(
                        CommandContext(
                            ctx.event, Utils.removeFirst(ctx.args)
                        )
                    )
                }
            }
        }
    }

    private fun getTag(guildId: Long, name: String): TagContent? = database
        .from(TagTable)
        .select(TagTable.content, TagTable.attachments)
        .where { (TagTable.guildId eq guildId) and (TagTable.name eq name.toLowerCase(Locale.ROOT)) }
        .map { TagContent(it[TagTable.content]!!, it[TagTable.attachments]!!) }.singleOrNull()

    private inner class TagContent(val content: String, val attachments: TextArray)
}

private class Create(private val database: Database, private val parent: Tag) : Command(
    "create", arrayOf("c"), Category.NONE,
    "Create a tag in a guild", "<tag name> <tag content>"
) {
    override fun run(ctx: CommandContext) {
        if (ctx.args.isEmpty() && (ctx.message.attachments.isEmpty() || ctx.args.size > 1)) {
            reply(
                ctx, arrayOf(
                    MessageEmbed.Field("Command usage:", "$name $arguments", true)
                ), "Tag"
            )
            return
        }

        val name = ctx.args[0].toLowerCase(Locale.ROOT)

        if (parent.subCommands.keys.contains(ctx.args[0]) || database.from(TagTable).select(TagTable.name)
                .where { TagTable.name eq name }
                .map { it[TagTable.name] }.isNotEmpty()
        ) {
            replyError(ctx, "That name is unavailable.", "Tag")
            return
        }

        database.insert(TagTable) {
            set(it.name, name)
            set(it.guildId, ctx.guild!!.idLong)
            set(it.authorId, ctx.user.idLong)
            if (ctx.message.attachments.isNotEmpty()) set(
                it.attachments,
                ctx.message.attachments.map(Message.Attachment::getUrl).toTypedArray()
            )
            set(
                it.content,
                Utils.removeFirst(ctx.args).joinToString(" ")
            )
        }
        reply(ctx, "Tag created.", "Tag")
    }
}

private class Remove(private val database: Database) : Command(
    "remove", arrayOf("r", "delete", "d"), Category.NONE,
    "Removes a tag (only if the tag is yours)", "<tag name>"
) {
    override fun run(ctx: CommandContext) {
        if (ctx.args.isEmpty()) {
            reply(
                ctx, arrayOf(
                    MessageEmbed.Field("Command usage:", "$name $arguments", true)
                ), "Tag"
            )
            return
        }

        database.delete(TagTable) {
            (TagTable.authorId eq ctx.user.idLong) and (TagTable.guildId eq ctx.guild!!.idLong) and (TagTable.name eq ctx.args[0].toLowerCase(
                Locale.ROOT
            ))
        }

        reply(ctx, "Tag deleted (if it existed and was yours).", "Tag")
    }
}

private class List(private val database: Database) : Command(
    "list", arrayOf("l"), Category.NONE,
    "Lists all the tags in a guild", ""
) {
    override fun run(ctx: CommandContext) {
        val tags = database.from(TagTable).select(TagTable.name).where { TagTable.guildId eq ctx.guild!!.idLong }
            .map { it[TagTable.name]!!.toLowerCase(Locale.ROOT) }
        val builder = StringBuilder()

        Paginator.createMenu("Tags", mutableListOf<Page>().apply {
            tags.forEachIndexed { i, s ->
                if (i % 10 == 0 && i != 0) {
                    add(Page(builder.toString()))
                    builder.clear()
                } else if (i == tags.size - 1) {
                    builder.append(s)
                    add(Page(builder.toString()))

                    return@forEachIndexed
                }

                builder.append(s).append("\n")
            }
        }, ctx.channel, ctx.user.idLong)
    }
}

private class Owner(private val database: Database) : Command(
    "owner", arrayOf("o"), Category.NONE,
    "Shows the owner of the command", "<tag name>"
) {
    override fun run(ctx: CommandContext) {
        if (ctx.args.isEmpty()) {
            reply(
                ctx, arrayOf(
                    MessageEmbed.Field("Command usage:", "$name $arguments", true)
                ), "Tag"
            )
            return
        }

        val authorId = database.from(TagTable)
            .select(TagTable.authorId)
            .where { (TagTable.guildId eq ctx.guild!!.idLong) and (TagTable.name eq ctx.args[0].toLowerCase(Locale.ROOT)) }
            .map { it[TagTable.authorId] }.singleOrNull()

        if (authorId == null) {
            replyError(ctx, "There is no such tag!", "Tag")
            return
        }

        reply(ctx, "The owner of `${ctx.args[0]}` is <@!${authorId}>", "Tag")
    }
}

private class Edit(private val database: Database) : Command(
    "edit", arrayOf("e"), Category.NONE,
    "Edits a tag", "<tag name> <new content>"
) {
    override fun run(ctx: CommandContext) {
        if (ctx.args.isEmpty() && (ctx.message.attachments.isNotEmpty() || ctx.args.size > 1)) {
            reply(
                ctx, arrayOf(
                    MessageEmbed.Field("Command usage:", "$name $arguments", true)
                ), "Tag"
            )
            return
        }

        database.update(TagTable) {
            where {
                (TagTable.authorId eq ctx.user.idLong) and (TagTable.guildId eq ctx.guild!!.idLong) and (TagTable.name eq ctx.args[0].toLowerCase(
                    Locale.ROOT
                ))
            }

            set(TagTable.content, Utils.removeFirst(ctx.args).joinToString(" "))
            set(TagTable.attachments, ctx.message.attachments.map(Message.Attachment::getUrl).toTypedArray())
        }

        reply(ctx, "Tag edited (if it's yours).", "Tag")
    }
}

private class Search(private val database: Database) : Command(
    "search", arrayOf("s", "find"), Category.NONE,
    "Searches for tags", "<search query>"
) {
    override fun run(ctx: CommandContext) {
        if (ctx.args.isEmpty()) {
            reply(
                ctx, arrayOf(
                    MessageEmbed.Field("Command usage:", "$name $arguments", true)
                ), "Tag"
            )
            return
        }

        val matched = database.from(TagTable)
            .select(TagTable.name)
            .where { (TagTable.guildId eq ctx.guild!!.idLong) and (TagTable.name like "%${ctx.args[0].toLowerCase(Locale.ROOT)}%") }
            .map { it[TagTable.name] }

        if (matched.isEmpty()) {
            replyError(ctx, "Nothing found!", "Tag")
        } else {
            reply(ctx, matched.joinToString(", ", prefix = "**Matches:** "), "Tag")
        }
    }
}