package net.spacydev.bingo.bingo

import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

object BingoManager {

    private val cards = mutableMapOf<UUID, BingoCard>()

    fun getCard(player: Player): BingoCard {
        return cards.getOrPut(player.uniqueId) {
            BingoCard.random()
        }
    }

    fun markItem(player: Player, material: Material) {
        val card = cards[player.uniqueId] ?: return
        card.mark(material)

        if (card.hasBingo()) {
            player.sendMessage("§a§lBINGO! Du hast gewonnen.")
        }
    }
}
