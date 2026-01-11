package net.spacydev.bingo.bingo

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object BingoGUI {

    fun open(player: Player) {
        val card = BingoManager.getCard(player)
        val inv = Bukkit.createInventory(null, 27, "§6Bingo")

        card.getItems().forEachIndexed { i, mat ->
            val item = ItemStack(mat)
            val meta = item.itemMeta!!
            meta.setDisplayName(
                if (card.isMarked(i)) "§a✔ ${mat.name}"
                else "§c✖ ${mat.name}"
            )
            item.itemMeta = meta
            inv.setItem(i, item)
        }

        player.openInventory(inv)
    }
}
