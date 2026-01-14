package net.spacydev.bingo.bingo

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object BingoGUI {

    val TITLE: Any
        get() {
            TODO()
        }

    fun open(player: Player) {
        val round = BingoManager.getRound(player) ?: return
        val card = round.cards[player.uniqueId] ?: return

        val inv = Bukkit.createInventory(null, 27, "§6Bingo")

        card.items.take(27).forEachIndexed { index, material ->
            val item = ItemStack(material)
            val meta = item.itemMeta ?: return@forEachIndexed

            val name = material.name
                .lowercase()
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }

            if (card.marked[index]) {
                meta.setDisplayName("§a✔ $name")
                meta.addEnchant(Enchantment.UNBREAKING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } else {
                meta.setDisplayName("§c✖ $name")
            }

            item.itemMeta = meta
            inv.setItem(index, item)
        }

        player.openInventory(inv)
    }
}