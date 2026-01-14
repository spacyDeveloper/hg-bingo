package net.spacydev.bingo.bingo

import bingo.bingo.BingoCard
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

data class Round(
    val id: UUID,
    val worldName: String,
    val players: MutableSet<UUID> = mutableSetOf(),
    val cards: MutableMap<UUID, BingoCard> = mutableMapOf()
)

object BingoManager {

    private val rounds = mutableMapOf<UUID, Round>()
    private val playerRound = mutableMapOf<UUID, UUID>()
    private val savedOffhand = mutableMapOf<UUID, ItemStack?>()

    fun getRound(player: Player): Round? {
        val roundId = playerRound[player.uniqueId] ?: return null
        return rounds[roundId]
    }

    fun getCard(player: Player): BingoCard? {
        val round = getRound(player) ?: return null
        return round.cards[player.uniqueId]
    }

    fun startRound(): UUID {
        val id = UUID.randomUUID()
        val world = BingoWorldManager.reserveWorldForRound(id)
        val round = Round(id, world.name)
        rounds[id] = round

        val pool = buildWeightedPool()

        Bukkit.getOnlinePlayers().forEach { player ->
            round.players.add(player.uniqueId)
            playerRound[player.uniqueId] = id
            player.teleport(world.spawnLocation)

            val card = BingoCard.random(pool)
            round.cards[player.uniqueId] = card

            giveBingoCard(player)
        }

        return id
    }

    fun endRound(roundId: UUID) {
        val round = rounds.remove(roundId) ?: return

        round.players.forEach { uuid ->
            Bukkit.getPlayer(uuid)?.let { restoreOffhand(it) }
            playerRound.remove(uuid)
        }

        BingoWorldManager.releaseWorld(roundId)
    }

    private fun giveBingoCard(player: Player) {
        savedOffhand[player.uniqueId] = player.inventory.itemInOffHand
        player.inventory.setItemInOffHand(BingoItems.cardItem())
    }

    private fun restoreOffhand(player: Player) {
        val old = savedOffhand.remove(player.uniqueId)
        player.inventory.setItemInOffHand(old)
    }

    fun markItem(player: Player, material: Material) {
        val roundId = playerRound[player.uniqueId] ?: return
        val round = rounds[roundId] ?: return
        val card = round.cards[player.uniqueId] ?: return

        if (!card.mark(material)) return

        if (player.openInventory.title == BingoGUI.TITLE) {
            BingoGUI.open(player)
        }

        if (card.hasBingo()) {
            Bukkit.broadcastMessage("§a§lBINGO! §r${player.name} hat gewonnen!")
            endRound(roundId)
        }
    }

    private fun buildWeightedPool(): List<Material> {
        val valid = Material.values().filter { isValidBingoMaterial(it) }
        val weighted = mutableListOf<Material>()

        for (mat in valid) {
            repeat(difficultyWeight(mat)) {
                weighted.add(mat)
            }
        }
        return weighted
    }

    private fun difficultyWeight(mat: Material): Int {
        val name = mat.name
        return when {
            name.contains("NETHERITE") || name.contains("NETHER_STAR") -> 5
            name.contains("DIAMOND") || name.contains("HEART_OF_THE_SEA") -> 4
            name.contains("EMERALD") || name.contains("ANVIL") -> 3
            name.contains("IRON") || name.contains("GOLD") -> 2
            else -> 1
        }
    }

    private fun isValidBingoMaterial(mat: Material): Boolean {
        if (!mat.isItem || mat.isAir) return false

        val forbidden = listOf(
            "SPAWN_EGG",
            "COMMAND",
            "BARRIER",
            "STRUCTURE",
            "DEBUG",
            "JIGSAW",
            "VOID"
        )

        return forbidden.none { mat.name.contains(it) }
    }
}
