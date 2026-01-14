package net.spacydev.bingo.bingo

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.util.*
import kotlin.collections.ArrayDeque

object BingoWorldManager {

    private val pool: ArrayDeque<World> = ArrayDeque()
    private val active = mutableMapOf<UUID, World>()
    private const val PREGENERATE = 3
    private const val WORLDBORDER_SIZE = 2000.0

    fun preGenerate() {
        for (i in 0 until PREGENERATE) {
            pool.add(createNewWorld("bingo_pool_$i"))
        }
    }

    private fun createNewWorld(baseName: String): World {
        val baseName_ = ""
        val name = "$baseName_${System.currentTimeMillis()}"
        val creator = WorldCreator(name)
            .environment(World.Environment.NORMAL)
            .type(WorldType.NORMAL)
            .generateStructures(true)
        val world = Bukkit.createWorld(creator)!!
        // set a default worldborder
        val wb = world.worldBorder
        wb.setCenter(0.0, 0.0)
        wb.size = WORLDBORDER_SIZE
        return world
    }

    fun reserveWorldForRound(roundId: UUID): World {
        val world = if (pool.isEmpty()) createNewWorld("bingo_pool_extra") else pool.removeFirst()
        active[roundId] = world
        return world
    }

    fun releaseWorld(roundId: UUID) {
        val world = active.remove(roundId) ?: return
        Bukkit.unloadWorld(world, false)
    }

    fun recycleWorld(world: World) {
        // put back into pool if still present on disk
        pool.addLast(world)
    }
}
