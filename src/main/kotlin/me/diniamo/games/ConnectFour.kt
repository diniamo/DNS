package me.diniamo.games
/*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.ThreadLocalRandom
import javax.annotation.Nonnull

class ConnectFour : ListenerAdapter() {
    override fun onGuildMessageReceived(@Nonnull event: GuildMessageReceivedEvent) {
        val message = event.message.contentRaw.split(" ")
        if (message[0].equals("<>connect4", ignoreCase = true)) {
            //event.getChannel().sendMessage(":red_circle: - player1\n:blue_circle: - player2\n:o: - table").queue();
            if (message.size == 1) {
                event.channel.sendMessage("Usage: <>connect4 <User you want to play with>").queue()
            } else {
                if (event.message.mentionedMembers.size == 1) {
                    if (!event.message.mentionedMembers[0].user.isBot) {
                        event.jda.addEventListener(Game(event.channel, event.member!!, event.message.mentionedMembers[0]))
                    } else {
                        event.channel.sendMessage("You can't play against a bot!").queue()
                    }
                } else {
                    event.channel.sendMessage("You can't play with more then 1 people.").queue()
                }
            }
        }
    }

}

internal class Game(private var channel: TextChannel, private val player1: Member, private val player2: Member) : ListenerAdapter() {
    private enum class GameState {
        PLAYER_ONE {
            override fun otherState() = PLAYER_ONE_SECOND
        },
        PLAYER_ONE_SECOND {
            override fun otherState() = PLAYER_ONE_SECOND
        },
        PLAYER_TWO {
            override fun otherState() = GameState.PLAYER_ONE_SECOND
        },
        PLAYER_TWO_SECOND {
            override fun otherState() = Game.GameState.PLAYER_ONE_SECOND
        };

        abstract fun otherState(): GameState;
    }

    private var state: GameState
    private val emojis: Map<Member, String> = hashMapOf(player1 to ":red_circle:", player2 to ":blue_circle:")
    private var game = arrayOf(
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"),
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"),
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"),
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"),
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"),
            arrayOf(":o:", ":o:", ":o:", ":o:", ":o:", ":o:", ":o:"))

    init {

        val starting = if (ThreadLocalRandom.current().nextInt(1, 3) == 1) player1 else player2

        state = if (starting === player1) GameState.PLAYER_ONE else GameState.PLAYER_TWO
        printTable()
        channel.sendMessage("""
            ${starting.asMention}, where do you want to place?
            You can chose 1-7!
            """.trimIndent()).queue()
    }

    private fun win(winner: Member, jda: JDA) {
        channel.sendMessage(winner.asMention + " wins!").queue()
        jda.removeEventListener(this)
    }

    override fun onMessageReceived(@Nonnull event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val member = event.member
        if (member == playerByState && event.channel == channel) {
            when (event.message.contentStripped) {
                "1", "2", "3", "4", "5", "6", "7" -> {
                    var num = event.message.contentStripped.toInt()

                    // Check: is the line full?
                    if (game[0][--num] != ":o:") {
                        channel.sendMessage("You can't put that there!").queue()
                        if (isSecondState()) {
                            win(getPlayerByState(otherState), event.jda)
                            return
                        }
                        state = secondState!!
                        return
                    }

                    // Placing mechanism
                    var first = 0
                    var second = 0
                    var i: Byte = 5
                    while (i >= 0) {
                        if (game[i.toInt()][num] == ":o:") {
                            game[i.toInt()][num] = emojis[member]!!
                            first = i.toInt()
                            second = num
                            break
                        }
                        i--
                    }
                    checkWinner(first, second, event.jda)
                    state = otherState
                    printTable()
                    channel.sendMessage("""
                        ${playerByState.asMention}, where do you want to place?
                        You can chose 1-7!
                        """.trimIndent()).queue()
                }
                else -> {
                    if (isSecondState()) {
                        win(getPlayerByState(otherState), event.jda)
                        return
                    }
                    channel.sendMessage("That's not a valid location.").queue()
                    state = secondState!!
                }
            }
        }
    }

    private val matracies = arrayOf(
            arrayOf(
                    arrayOf(-3, 0), arrayOf(-2, 0), arrayOf(-1, 0), arrayOf(0, 0), arrayOf(1, 0), arrayOf(2, 0), arrayOf(3, 0)
            ),
            arrayOf(
                    arrayOf(-3, 3), arrayOf(-2, 2), arrayOf(-1, 1), arrayOf(0, 0), arrayOf(1, -1), arrayOf(2, -2), arrayOf(3, -3)
            ),
            arrayOf(
                    arrayOf(0, 3), arrayOf(0, 2), arrayOf(0, 1), arrayOf(0, 0), arrayOf(0, -1), arrayOf(0, -2), arrayOf(0, -3)
            ),
            arrayOf(
                    arrayOf(3, 3), arrayOf(2, 2), arrayOf(1, 1), arrayOf(0, 0), arrayOf(-1, -1), arrayOf(-2, -2), arrayOf(-3, -3)
            )/*,
            arrayOf(
                    arrayOf(3, 0), arrayOf(2, 0), arrayOf(1, 0), arrayOf(0, 0), arrayOf(-1, 0), arrayOf(-2, 0), arrayOf(-3, 0)
            ),
            arrayOf(
                    arrayOf(3, 3), arrayOf(2, 2), arrayOf(1, 1), arrayOf(0, 0), arrayOf(-1, -1), arrayOf(-2, -2), arrayOf(-3, -3)
            )*/
    )

    private fun checkGameOver(lastX: Int, lastY: Int): Boolean {
        for (matrix in matracies) {  // row matrix, column matrix and diagonal matrices
            val hits = getHitsInOrder(matrix, lastX, lastY)
            if (hits >= 4) {
                return true
            }
        }
        return false
    }

    private fun getHitsInOrder(sourceMatrix: Array<IntArray>, srcX: Int, srcY: Int): Int {
        var hitsInOrder = 0
        var maxHits = 0
        val currentPlayerColour: Char = getCurrentPlayerColour()
        for (matrixElement in sourceMatrix) {
            val colorOfField = getColorOfField(matrixElement, srcX, srcY)
            if (colorOfField == currentPlayerColour) {
                hitsInOrder++
            } else {
                maxHits = maxHits.coerceAtLeast(hitsInOrder)
                hitsInOrder = 0
            }
        }
        return maxHits.coerceAtLeast(hitsInOrder)
    }

    private fun getColorOfField(matrixElement: IntArray, srcX: Int, srcY: Int): Char {
        val x = srcX + matrixElement[0]
        val y = srcY + matrixElement[1]
        val inRange: Boolean = isInRange(x, y)
        return if (inRange) getCharAtPosition(x, y) else EMPTY_CHAR
    }

    //var iteration = 0
    private fun checkWinner(first: Int, second: Int, jda: JDA) {
        val toCheck = arrayOf<IntArray>()

        /*for (byte i = (byte) (first == 0 ? first : first - 1); i < (first == 5 ? first : first + 1); i++) {
            for (byte j = (byte) (second == 0 ? second : second - 1); j < (second == 6 ? second : second + 1); j++) {
                System.out.println(String.format("Checking field %s, %s (Value: %s), around %s, %s\nPlayer placed: %s", i, j, game[i][j], first, second, getPlayerByState().getEffectiveName()));
                if (game[i][j].equals(game[first][second]) && i != firstBefore && j != secondBefore) {
                    if (iteration == 3) {
                        System.out.println("Calling winner from checkWinner method...");
                        win(getPlayerByState(), jda);
                        return;
                    } else {
                        System.out.println("Incrementing iteration...");
                        iteration++;
                        checkWinner(i, j, first, second, jda);
                        iteration = 0;
                        System.out.println("\n");
                    }
                }
            }
        }
        iteration = 0;*/
    }

    private fun printTable() {
        val eb = EmbedBuilder()
                .setTitle("Connect4")
        val table = StringBuilder()
        for (strings in game) {
            for (string in strings) {
                table.append(string)
            }
            table.append("\n")
        }
        eb.addField(player1.effectiveName + " vs. " + player2.effectiveName, String(table), false)
        eb.setFooter(playerByState.effectiveName + "'s turn")
        channel.sendMessage(eb.build()).queue()
    }


}*/