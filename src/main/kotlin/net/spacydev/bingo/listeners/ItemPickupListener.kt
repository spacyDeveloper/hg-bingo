package net.spacydev.bingo.listeners

import bingo.bingo.BingoManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.entity.Player

class ItemPickupListener : Listener {

    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        BingoManager.markItem(player, event.item.itemStack.type)
    }
}
