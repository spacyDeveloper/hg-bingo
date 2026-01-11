package bingo.bingo

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.util.*

object BingoWorldManager {

    private val activeWorlds = mutableMapOf<UUID, World>()

    fun createWorldForPlayer(playerId: UUID): World {
        val worldName = "bingo_${playerId}_${System.currentTimeMillis()}"
        val creator = WorldCreator(worldName)
            .environment(World.Environment.NORMAL)
            .type(WorldType.NORMAL)
            .generateStructures(true)

        val world = Bukkit.createWorld(creator)!!
        activeWorlds[playerId] = world
        return world
    }

    fun getWorld(playerId: UUID): World? {
        return activeWorlds[playerId]
    }

    fun removeWorld(playerId: UUID) {
        val world = activeWorlds.remove(playerId) ?: return
        Bukkit.unloadWorld(world, false)
    }
}
