package me.diniamo.commands.system

import net.dv8tion.jda.api.JDA
import me.diniamo.Values

class AnswerCache<K, V>(private val jda: JDA) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
        val maxSize = jda.guildCache.size() * 5

        return size > maxSize
    }
}