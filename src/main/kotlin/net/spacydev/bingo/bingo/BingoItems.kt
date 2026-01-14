package net.spacydev.bingo.bingo

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object BingoItems {

    fun cardItem(): ItemStack {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta ?: return item

        meta.setDisplayName("§6§lBingo-Karte")
        meta.lore = listOf(
            "§7Rechtsklick, um",
            "§7deine Bingo-Karte",
            "§7zu öffnen"
        )

        meta.addItemFlags(
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_ENCHANTS
        )

        item.itemMeta = meta
        return item
    }

    fun isCard(item: ItemStack?): Boolean {
        if (item == null || item.type != Material.PAPER) return false
        val meta = item.itemMeta ?: return false
        return meta.displayName == "§6§lBingo-Karte"
    }
}
