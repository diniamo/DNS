package me.diniamo.commands
/*
import me.diniamo.commands.system.Category
import me.diniamo.commands.system.Command
import me.diniamo.commands.system.CommandContext
import me.diniamo.commands.system.GUILD_NULL
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import org.ktorm.database.Database
import org.ktorm.schema.Table
import java.sql.*
import kotlin.system.exitProcess

const val SUBCOMMAND = "Subcommand of tag (execute the command tag for help)"

object TagTable : Table<Nothing>("tags") {
    val
}

class Tag(jda: JDA, private val link: String, private val driver: String) : Command(
    "tag", arrayOf("t"), Category.UTILITY,
    "Tag system", "<sucommand> <additional arguments>"
) {
    private var connection: Connection
    private var statement: Statement

    init {
        try {
            val cp = Database.connect("")
            connection = cp.connection
            statement = connection.createStatement()
        } catch (ex: SQLException) {
            println("Error occurred while connecting to the database: $ex")
            jda.presence.setPresence(OnlineStatus.OFFLINE, null)
            jda.shutdown()
            exitProcess(1)
        }
    }

    override fun run(ctx: CommandContext) {
        require(ctx.guild != null) { GUILD_NULL }

        if (ctx.args.isEmpty()) {
            reply(ctx, templateBuilder(ctx)
            .setTitle("Tag command")
                .appendDescription("With this command you can create tags (global), with a text value. Later on on you can get the text value by their name. Usage:")
                .addField("Create", "With this subcommand, you can create tags.", false)
                .addField("Remove", "With this subcommand, you can remove tags (only if they were created by you).", false)
                .addField("List", "With this subcommand, you can get the list of all the tags.", false).build())
        } else {
            reply(ctx, getTag(ctx.guild.id, event.args), "")
        }
    }

    private fun getTag(guildId: String, name: String): String {
        val rs = statement.executeQuery("SELECT value FROM $guild WHERE name='$name'")

        return rs.getString("value")
    }
}

private class Create(private val conn: Connection, private val jda: JDA) : Command() {
    init {
        name = "create"
        aliases = arrayOf("c")
        help = "Subcommand of tag (execute the command tag for help)"
    }

    private val statement = conn.createStatement()
    private lateinit var pStatement: PreparedStatement;

    override fun execute(event: CommandEvent) {
        //event.reply(event.args.split(' ').joinToString(separator = " "))

        try {
            // Creating the table if doesn't exist
            /*val rs = conn.metaData.getTables(null, null, event.guild.id, null)
            if (!rs.next()) {
                statement.execute("create table ${event.guild.id}(name varchar(20), value varchar(2000), creator long)")
            }*/
            statement.execute("CREATE TABLE IF NOT EXISTS ${event.guild.id}(name varchar(20), value varchar(2000), creator long, PRIMARY KEY(name))")

            addTag(event.guild.idLong, Tag.Column(event.args, event.args.substringAfter(' '), event.member.idLong))

            event.reply("Tag has been successfully created. \u2705")
        } catch (ex: Exception) {
            println("Error occurred while querying the database: $ex")
            jda.presence.setPresence(OnlineStatus.INVISIBLE, null)
            jda.shutdown()
            exitProcess(1)
        }
    }

    private fun addTag(guild: Long, column: Tag.Column) {
        //statement.execute("insert into $guild values ('$name', '$value', $creator)")
        pStatement = conn.prepareStatement("INSERT INTO $guild values (?, ?, ?)")
        pStatement.setString(1, column.name)
        pStatement.setString(2, column.value)
        pStatement.setLong(3, column.creator)
        pStatement.execute()
    }
}

private class Remove(private val conn: Connection) : Command() {
    init {
        name = "remove"
        aliases = arrayOf("r")
        help = "Subcommand of tag (execute the command tag for help)"
    }

    override fun execute(event: CommandEvent) {
        val pStatement = conn.prepareStatement("DELETE FROM ${event.guild.id} name=?")
        pStatement.setString(1, "")
        pStatement.execute()
        event.reply("Done!")
    }
}

private class List(conn: Connection, private val jda: JDA) : Command() {
    init {
        name = "list"
        aliases = arrayOf("l")
        help = "Subcommand of tag (execute the command tag for help)"
    }

    private val statement = conn.createStatement()

    override fun execute(event: CommandEvent) {
        val builder = StringBuilder()
        try {
            // TODO: Better list command output

            for (column in getAllTags(event.guild.id)) {
                builder.append("${column.name}: ${column.value}\n")
            }
            event.reply(builder.toString())
        } catch (ex: SQLException) {
            event.reply("No such tag found")
        }
    }


    private fun getAllTags(guild: String): kotlin.collections.List<Tag.Column> {
        val toReturn: MutableList<Tag.Column> = mutableListOf()

        val rs = statement.executeQuery("SELECT * FROM $guild")
        while (rs.next()) {
            toReturn.add(Tag.Column(rs.getString("name"), rs.getString("value"), rs.getLong("creator")))
        }

        return toReturn
    }
}*/