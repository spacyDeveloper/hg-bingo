package bingo.bingo

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random

class BingoCard(val items: List<Material>) {

    val marked = BooleanArray(25)

    fun updateBingoState() {
        if (hasBingo()) {
            println("Bingo! 🎉")
        }
    }


    fun mark(material: Material): Boolean {
        val index = items.indexOf(material)
        if (index == -1) return false
        if (marked[index]) return false

        marked[index] = true
        updateBingoState()
        return true
    }


    fun hasBingo(): Boolean {
        val lines = listOf(
            intArrayOf(0,1,2,3,4),
            intArrayOf(5,6,7,8,9),
            intArrayOf(10,11,12,13,14),
            intArrayOf(15,16,17,18,19),
            intArrayOf(20,21,22,23,24),
            intArrayOf(0,5,10,15,20),
            intArrayOf(1,6,11,16,21),
            intArrayOf(2,7,12,17,22),
            intArrayOf(3,8,13,18,23),
            intArrayOf(4,9,14,19,24),
            intArrayOf(0,6,12,18,24),
            intArrayOf(4,8,12,16,20)
        )
        return lines.any { line -> line.all { marked[it] } }
    }

    fun toOffhandItem(): ItemStack {
        val paper = ItemStack(Material.PAPER)
        val meta = paper.itemMeta!!
        meta.setDisplayName("§6Bingo Karte")
        val lore = mutableListOf<String>()
        for (i in items.indices) {
            val name = items[i].name.lowercase().replace('_', ' ')
            val mark = if (marked[i]) "§a✔" else "§7✖"
            lore.add("$mark $name")
        }
        meta.lore = lore
        paper.itemMeta = meta
        return paper
    }

    companion object {
        fun random(pool: List<Material>): BingoCard {
            val chosen = mutableSetOf<Material>()
            val list = mutableListOf<Material>()
            val rng = Random.Default
            while (list.size < 25) {
                val m = pool[rng.nextInt(pool.size)]
                if (m !in chosen) {
                    chosen.add(m)
                    list.add(m)
                }
            }
            return BingoCard(list)
        }
    }
}
