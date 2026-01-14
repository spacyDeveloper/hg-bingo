package net.spacydev.bingo.bingo

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import kotlin.random.Random
import kotlin.streams.toList

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

    // start a round with all online players (simple approach)
    fun startRound() : UUID {
        val id = UUID.randomUUID()
        val world = BingoWorldManager.reserveWorldForRound(id)
        val round = Round(id, world.name)
        rounds[id] = round

        // teleport all online players (or filter perms)
        Bukkit.getOnlinePlayers().forEach { p ->
            round.players.add(p.uniqueId)
            playerRound[p.uniqueId] = id
            p.teleport(world.spawnLocation)
        }

        // create cards for each player using weighted pool
        val pool = buildWeightedPool()
        round.players.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid) ?: return@forEach
            val card = BingoCard.random(pool)
            round.cards[uuid] = card
            saveAndGiveOffhand(player, card.toOffhandItem())
        }

        return id
    }

    fun endRound(roundId: UUID) {
        val round = rounds.remove(roundId) ?: return
        // restore offhands
        round.players.forEach { uuid ->
            val p = Bukkit.getPlayer(uuid)
            if (p != null) {
                restoreOffhand(p)
            }
            playerRound.remove(uuid)
        }
        BingoWorldManager.releaseWorld(roundId)
    }

    private fun saveAndGiveOffhand(player: Player, item: ItemStack) {
        savedOffhand[player.uniqueId] = player.inventory.itemInOffHand
        player.inventory.setItemInOffHand(item)
    }

    private fun restoreOffhand(player: Player) {
        val old = savedOffhand.remove(player.uniqueId)
        player.inventory.setItemInOffHand(old)
    }

    fun markItem(player: Player, material: Material) {
        val rid = playerRound[player.uniqueId] ?: return
        val round = rounds[rid] ?: return
        val card = round.cards[player.uniqueId] ?: return
        card.mark(material)
        // update offhand item
        player.inventory.setItemInOffHand(card.toOffhandItem())
        if (card.hasBingo()) {
            Bukkit.broadcastMessage("§a§lBINGO! §r${player.name} hat gewonnen!")
            endRound(rid)
        }
    }

    // Build weighted pool: materials that are valid, each repeated by difficulty (or weight)
    private fun buildWeightedPool(): List<Material> {
        val valid = Material.values().filter { isValidBingoMaterial(it) }
        val weighted = mutableListOf<Material>()
        for (m in valid) {
            val w = difficultyWeight(m)
            repeat(w) { weighted.add(m) }
        }
        return weighted
    }

    private fun difficultyWeight(mat: Material): Int {
        val name = mat.name
        return when {
            name.contains("NETHERITE") || name.contains("DIAMOND") || name.contains("NETHER_STAR") -> 5
            name.contains("EMERALD") || name.contains("HEART_OF_THE_SEA") || name.contains("HELMET") -> 4
            name.contains("IRON") || name.contains("GOLD") || name.contains("ANVIL") -> 3
            name.contains("REDSTONE") || name.contains("LAPIS") || name.contains("COAL") -> 2
            else -> 1
        }
    }

    private fun isValidBingoMaterial(mat: Material): Boolean {
        if (mat.isAir) return false
        if (!mat.isItem) return false
        val s = mat.name
        val forbidden = listOf("SPAWN_EGG", "COMMAND", "BARRIER", "STRUCTURE", "DEBUG", "VOID", "CARRIER", "JIGSAW")
        if (forbidden.any { s.contains(it) }) return false
        return mat.maxStackSize > 0
    }
}
