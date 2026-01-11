package net.spacydev.bingo

import bingo.commands.BingoCommand
import bingo.listeners.ItemPickupListener
import org.bukkit.plugin.java.JavaPlugin

class BingoMain : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(ItemPickupListener(), this)
        getCommand("bingo")?.setExecutor(BingoCommand())

        logger.info("Bingo Plugin active")
    }
}
