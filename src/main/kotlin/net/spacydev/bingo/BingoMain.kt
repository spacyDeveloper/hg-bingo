package net.spacydev.bingo

import net.spacydev.bingo.commands.BingoCommand
import net.spacydev.bingo.listeners.ItemPickupListener
import org.bukkit.plugin.java.JavaPlugin

class BingoMain : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(ItemPickupListener(), this)
        getCommand("bingo")?.setExecutor(BingoCommand())

        logger.info("Bingo Plugin active")
    }
}
