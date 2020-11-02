package me.diniamo.commands.system

import net.dv8tion.jda.api.JDA

class AnswerCache<K, V>(private val jda: JDA) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
        val maxSize = jda.guildCache.size() * 5

        if(size > maxSize) return true
        return false
    }
}