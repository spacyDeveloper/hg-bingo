package net.spacydev.bingo.listeners

import net.spacydev.bingo.bingo.BingoManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class ItemInteractListener : Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val who = e.whoClicked as? Player ?: return
        val clicked = e.currentItem ?: return
        val clickedInv = e.clickedInventory ?: return

        if ((clickedInv.type == InventoryType.CHEST || clickedInv.type == InventoryType.ENDER_CHEST || clickedInv.type == InventoryType.SHULKER_BOX)
            && (e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY || e.action == InventoryAction.PICKUP_ALL || e.action == InventoryAction.PICKUP_ONE)
        ) {
            BingoManager.markItem(who, clicked.type)
        }
    }

    @EventHandler
    fun onEntityPickup(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        BingoManager.markItem(player, e.item.itemStack.type)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val drops = e.block.getDrops(player.inventory.itemInMainHand)
        for (d in drops) {
            BingoManager.markItem(player, d.type)
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val p = e.player
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        val b = e.clickedBlock ?: return

        if ((b.type == Material.BEEHIVE || b.type == Material.BEE_NEST)
            && p.inventory.itemInMainHand?.type == Material.SHEARS
        ) {
            BingoManager.markItem(p, Material.HONEYCOMB)
        }
    }
}
