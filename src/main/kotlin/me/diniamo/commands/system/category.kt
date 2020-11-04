package me.diniamo.commands.system

enum class Category(val categoryName: String, val emoji: String) {
    INFO("Info", "<:info:772105403373977601>"),
    UTILITY("Utility", "<:utility:772105627563982898>"),
    FUN("Fun", "\uD83E\uDD2A"),
    MEME("Meme", "<:trollface:772106035002867712>"),
    ADMIN("Admin", "<:admin:772379369791946762>")
}