package net.spacydev.bingo.bingo

import org.bukkit.Material

class BingoCard(private val items: List<Material>) {

    private val marked = BooleanArray(25)

    fun mark(material: Material) {
        items.forEachIndexed { index, mat ->
            if (mat == material) marked[index] = true
        }
    }

    fun hasBingo(): Boolean {
        val lines = listOf(
            listOf(0,1,2,3,4),
            listOf(5,6,7,8,9),
            listOf(10,11,12,13,14),
            listOf(15,16,17,18,19),
            listOf(20,21,22,23,24),
            listOf(0,5,10,15,20),
            listOf(1,6,11,16,21),
            listOf(2,7,12,17,22),
            listOf(3,8,13,18,23),
            listOf(4,9,14,19,24),
            listOf(0,6,12,18,24),
            listOf(4,8,12,16,20)
        )
        return lines.any { line -> line.all { marked[it] } }
    }

    fun getItems() = items
    fun isMarked(index: Int) = marked[index]

    companion object {
        fun random(): BingoCard {
            val pool = Material.values()
                .filter { it.isItem && !it.isAir }
                .shuffled()
                .take(25)
            return BingoCard(pool)
        }
    }
}
